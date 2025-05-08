package com.readhub.bookservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class KafkaTestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/send")
    public String sendMessage() {
        kafkaTemplate.send("test-topic", "[book-service]");
        return "통신 도착";
    }
}
