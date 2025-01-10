package com.readhub.backend.security.utils;

import org.springframework.security.authentication.DisabledException;

public class UserResignedException extends DisabledException {
    public UserResignedException(String msg) {
        super(msg);
    }
}
