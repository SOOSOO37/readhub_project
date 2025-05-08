package com.readhub.global.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseEvent {

    private Long userId;
    private String nickname;
    private String email;
    private String correlationId;

}
