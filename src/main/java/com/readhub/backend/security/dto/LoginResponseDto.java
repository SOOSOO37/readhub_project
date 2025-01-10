package com.readhub.backend.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private long id;
    private String email;
    private String nickName;

    @Builder
    public LoginResponseDto(long id, String email, String nickName) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
    }
}
