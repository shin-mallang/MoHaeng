package com.mohaeng.infrastructure.authentication.jwt.service.exception;

public class InvalidAccessTokenException extends RuntimeException {

    private static final String MESSAGE = "AccessToken이 유효하지 않습니다.";

    public InvalidAccessTokenException() {
        super(MESSAGE);
    }
}
