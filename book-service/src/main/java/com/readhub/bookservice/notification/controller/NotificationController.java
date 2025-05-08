package com.readhub.bookservice.notification.controller;

import com.readhub.bookservice.notification.service.NotificationService;
import com.readhub.global.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails user,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(user.getId(), lastEventId);
    }

    @PutMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId,
                                                    @AuthenticationPrincipal CustomUserDetails user) {

        boolean updated = notificationService.markNotificationAsRead(notificationId,user.getId());
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
