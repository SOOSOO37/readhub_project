package com.readhub.backend.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    USER_NICKNAME_EXISTS(409, "이미 등록된 닉네임 입니다."),

    USER_EMAIL_EXISTS(409, "이미 등록된 이메일 입니다."),

    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),

    USER_STOP(404, "정지된 회원입니다."),

    BOOK_NOT_FOUND(404,"대출할 수없는 도서입니다."),

    PAGE_NOT_FOUND(404,"접근할 수 없는 페이지 입니다."),

    BOOK_NOT_AVAILABLE(404,"대출 가능한 권수가 없습니다"),

    CATEGORY_EXISTS(409, "이미 등록된 카테고리 입니다."),

    USER_QUIT(404, "탈퇴한 회원입니다."),

    PERMISSION_NOT_EXIST(409, "허가되지 않은 접근입니다."),

    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),

    TOKEN_NOT_FOUND(404, "해당하는 토큰이 존재하지 않습니다."),

    LOGOUT_AUTHORIZATION(401, "로그아웃 되었습니다."),

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
