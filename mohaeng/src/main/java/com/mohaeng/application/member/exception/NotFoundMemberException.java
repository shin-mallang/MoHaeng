package com.mohaeng.application.member.exception;

public class NotFoundMemberException extends RuntimeException {

    private static final String MESSAGE = "회원이 존재하지 않습니다.";

    public NotFoundMemberException() {
        super(MESSAGE);
    }
}
