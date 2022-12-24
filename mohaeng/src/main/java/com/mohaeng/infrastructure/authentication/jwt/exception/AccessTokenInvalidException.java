package com.mohaeng.infrastructure.authentication.jwt.exception;

public class AccessTokenInvalidException extends RuntimeException {

    private static final String MESSAGE = "AccessToken이 유효하지 않습니다.";

    public AccessTokenInvalidException() {
        super(MESSAGE);
    }
}
