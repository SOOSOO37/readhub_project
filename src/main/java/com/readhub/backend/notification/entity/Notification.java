package com.readhub.backend.notification.entity;

import com.readhub.backend.global.audit.Auditable;
import com.readhub.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Notification{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @Builder
    public Notification(User receiver, NotificationType notificationType, String content, String url, Boolean isRead){
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }

    public enum NotificationType {
        DUE_SOON,           // 반납 일자가 임박
        AVAILABLE_TO_RENT,  // 예약 도서가 대여 가능
        OVERDUE             // 반납 일자 지남
    }
}
