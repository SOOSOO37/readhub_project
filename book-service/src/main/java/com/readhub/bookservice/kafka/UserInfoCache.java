package com.readhub.bookservice.kafka;

import com.readhub.global.kafka.UserInfoResponseEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class UserInfoCache {

    private final Map<String, CompletableFuture<UserInfoResponseEvent>> cache = new ConcurrentHashMap<>();

    public CompletableFuture<UserInfoResponseEvent> createFuture(String correlationId) {
        CompletableFuture<UserInfoResponseEvent> future = new CompletableFuture<>();
        cache.put(correlationId, future);
        return future;
    }

    public void completeResponse(UserInfoResponseEvent event) {
        CompletableFuture<UserInfoResponseEvent> future = cache.remove(event.getCorrelationId());
        if (future != null) {
            future.complete(event);
        }
    }
}
