package com.mohaeng.authentication.application.exception;

public class IncorrectAuthenticationException extends RuntimeException {

    private static final String MESSAGE = "인증 정보가 올바르지 않아 로그인에 실패하였습니다.";

    public IncorrectAuthenticationException() {
        super(MESSAGE);
    }
}
