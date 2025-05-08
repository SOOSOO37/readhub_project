package com.readhub.bookservice.kafka;

import com.readhub.global.kafka.UserInfoResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
@Slf4j
@Component
@RequiredArgsConstructor
public class UserInfoConsumer {

    private final UserInfoCache userInfoCache;

    @KafkaListener(topics = "user-info-response", groupId = "book-service")
    public void consume(UserInfoResponseEvent responseEvent) {
        log.info("[Consumer] user-info-response 수신완료: {}", responseEvent);
        userInfoCache.completeResponse(responseEvent);
    }
}
