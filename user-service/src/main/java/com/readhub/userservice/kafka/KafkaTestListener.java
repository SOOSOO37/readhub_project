package com.readhub.userservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestListener {

    @KafkaListener(topics = "test-topic", groupId = "user-service-group")
    public void listenTest(String message) {
        System.out.println("메세지 수신 = " + message);
    }
}
