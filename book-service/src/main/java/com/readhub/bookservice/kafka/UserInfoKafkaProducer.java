package com.readhub.bookservice.kafka;

import com.readhub.global.kafka.UserInfoRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserInfoKafkaProducer {

    private final KafkaTemplate<String, UserInfoRequestEvent> kafkaTemplate;

    public void requestUserInfo(Long userId, String correlationId) {
        UserInfoRequestEvent event = new UserInfoRequestEvent(userId, correlationId);
        kafkaTemplate.send("user-info-request", event);
    }
}
