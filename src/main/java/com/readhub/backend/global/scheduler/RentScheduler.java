package com.readhub.backend.global.scheduler;

import com.readhub.backend.notification.entity.Notification;
import com.readhub.backend.notification.repository.NotificationRepository;
import com.readhub.backend.notification.service.NotificationService;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rent.repository.RentRepository;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RentScheduler {

    private final RentRepository rentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *")
    public void runNotificationJobs() {
        sendDueSoonNotifications();
        sendOverdueNotifications();
    }

    private void sendDueSoonNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Rent> dueSoonRents = rentRepository.findByDueDateAndRentStatus(tomorrow, Rent.RentStatus.RENT);

        int count = 0;
        for (Rent rent : dueSoonRents) {
            count += sendNotification(rent, Notification.NotificationType.DUE_SOON, "의 반납일이 하루 남았습니다.");
        }
        log.info("[반납임박 알림] 전송 완료 - {}건", count);
    }

    private void sendOverdueNotifications() {
        LocalDate today = LocalDate.now();
        List<Rent> overdueRents = rentRepository.findByDueDateBeforeAndRentStatus(today, Rent.RentStatus.RENT);

        int count = 0;
        for (Rent rent : overdueRents) {
            count += sendNotification(rent, Notification.NotificationType.OVERDUE, " 도서가 연체되었습니다.");
        }
        log.info("[연체 알림] 전송 완료 - {}건", count);
    }

    private int sendNotification(Rent rent, Notification.NotificationType type, String messageSuffix) {
        User user = rent.getUser();
        int sentCount = 0;

        for (RentBook rentBook : rent.getRentBookList()) {
            String bookTitle = rentBook.getBook().getTitle();
            String content = "'" + bookTitle + "'" + messageSuffix;
            String url = "/books/" + rentBook.getBook().getId();

            boolean exists = notificationRepository.existsByReceiverAndNotificationTypeAndContent(user, type, content);
            if (exists) continue;

            try {
                notificationService.send(user, type, content, url);
                sentCount++;
            } catch (Exception e) {
                log.warn("[알림 전송 실패] type={}, userId={}, bookId={}, error={}", type, user.getId(), rentBook.getBook().getId(), e.getMessage());
            }
        }

        return sentCount;
    }
}


