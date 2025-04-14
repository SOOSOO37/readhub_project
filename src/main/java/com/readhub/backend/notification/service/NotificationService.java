package com.readhub.backend.notification.service;

import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.notification.entity.Notification;
import com.readhub.backend.notification.repository.EmitterRepository;
import com.readhub.backend.notification.repository.NotificationRepository;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static com.readhub.backend.notification.mapper.NotificationMapper.NOTIFICATION_MAPPER;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emtterId = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emtterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emtterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emtterId));

        sendToClient(emitter, emtterId, "EventStream Created. [userId=" + userId + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    public void send(User receiver, Notification.NotificationType notificationType, String content, String url) {
        Notification notification = notificationRepository.save(createNotification(receiver, notificationType, content, url));
        String userId = String.valueOf(receiver.getId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(userId);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, NOTIFICATION_MAPPER.NotificationToNotificationResponseDto(notification));
                }
        );
    }

    private Notification createNotification(User receiver, Notification.NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }

    public boolean markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);

        if (notification == null) return false;

        Long receiverId = notification.getReceiver().getId();
        if (!receiverId.equals(userId)) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        return true;
    }


}
