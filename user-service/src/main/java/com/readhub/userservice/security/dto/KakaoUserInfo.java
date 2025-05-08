package com.readhub.userservice.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfo {

    private String email;
    private String nickname;
    private String profileImage;
}
