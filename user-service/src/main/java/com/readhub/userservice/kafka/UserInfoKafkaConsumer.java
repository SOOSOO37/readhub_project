package com.readhub.userservice.kafka;

import com.readhub.global.kafka.UserInfoRequestEvent;
import com.readhub.global.kafka.UserInfoResponseEvent;
import com.readhub.userservice.user.entity.User;
import com.readhub.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserInfoKafkaConsumer {

    private final KafkaTemplate<String, UserInfoResponseEvent> kafkaTemplate;
    private final UserRepository userRepository;

    @KafkaListener(topics = "user-info-request", groupId = "user-service-group")
    public void consumeUserInfoRequest(UserInfoRequestEvent event) {
        Optional<User> userInfo = userRepository.findById(event.getUserId());

        if (userInfo.isPresent()) {
            User user = userInfo.get();
            UserInfoResponseEvent response = new UserInfoResponseEvent(
                    user.getId(), user.getNickName(), user.getEmail(), event.getCorrelationId()
            );
            kafkaTemplate.send("user-info-response", response);
        }
    }
}
