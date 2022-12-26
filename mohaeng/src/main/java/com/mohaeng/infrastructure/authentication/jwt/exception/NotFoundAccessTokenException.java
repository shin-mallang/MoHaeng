package com.mohaeng.infrastructure.authentication.jwt.exception;

public class NotFoundAccessTokenException extends RuntimeException {

    private static final String MESSAGE = "AccessToken이 존재하지 않습니다.";

    public NotFoundAccessTokenException() {
        super(MESSAGE);
    }
}
