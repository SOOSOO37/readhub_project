package com.readhub.bookservice.notification.service;

import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.notification.entity.Notification;
import com.readhub.bookservice.notification.mapper.NotificationMapper;
import com.readhub.bookservice.notification.repository.EmitterRepository;
import com.readhub.bookservice.notification.repository.NotificationRepository;
import com.readhub.global.kafka.UserInfoResponseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final String CONNECTION_EVENT_MESSAGE = "EventStream Created. [userId=%d]";
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;
    private final UserInfoKafkaService userInfoKafkaService;

    public SseEmitter subscribe(Long userId, String lastEventId) {
        SseEmitter emitter = createEmitter(userId);
        sendToClient(emitter, userId + "", String.format(CONNECTION_EVENT_MESSAGE, userId));
        sendMissedEvents(userId, lastEventId, emitter);
        return emitter;
    }

    public void send(Long receiverId, Notification.NotificationType type, String content, String url) {
        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(receiverId);
        Notification notification = createAndSaveNotification(user.getUserId(), type, content, url);

        String keyPrefix = String.valueOf(user.getUserId());
        emitterRepository.findAllEmitterStartWithByUserId(keyPrefix).forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            sendToClient(emitter, key, mapper.NotificationToNotificationResponseDto(notification));
        });
    }

    public boolean markNotificationAsRead(Long notificationId, Long receiverId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getReceiverId().equals(receiverId)) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        return true;
    }

    private SseEmitter createEmitter(Long userId) {
        String emitterId = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(emitterId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        return emitter;
    }

    private void sendMissedEvents(Long userId, String lastEventId, SseEmitter emitter) {
        if (lastEventId.isEmpty()) return;

        emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId)).entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
    }

    private Notification createAndSaveNotification(Long receiverId, Notification.NotificationType type, String content, String url) {
        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .notificationType(type)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event().id(emitterId).data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
        }
    }
}
