package com.readhub.bookservice.kafka;

import com.readhub.global.kafka.UserInfoResponseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class UserInfoKafkaService {
    private final UserInfoKafkaProducer userInfoKafkaProducer;
    private final UserInfoCache userInfoCache;

    public UserInfoResponseEvent fetchUserInfoViaKafka(Long userId) {
        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<UserInfoResponseEvent> future = userInfoCache.createFuture(correlationId);

        userInfoKafkaProducer.requestUserInfo(userId, correlationId);

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("Kafka 응답 시간 초과", e);
        } catch (Exception e) {
            throw new RuntimeException("Kafka 응답 실패", e);
        }
    }
}
