package com.readhub.backend.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    USER_NICKNAME_EXISTS(409, "이미 등록된 닉네임입니다."),

    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),

    USER_QUIT(404, "탈퇴한 회원입니다."),

    PERMISSION_NOT_EXIST(409, "허가되지 않은 접근입니다."),

    SLEEPER_ACCOUNT(404, "휴면계정 입니다.");


    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
