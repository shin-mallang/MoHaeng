package com.mohaeng.member.exception;

public class DuplicateUsernameException extends RuntimeException {

    private static final String MESSAGE = "이미 존재하는 아이디입니다.";

    public DuplicateUsernameException() {
        super(MESSAGE);
    }
}
