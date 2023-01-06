package com.mohaeng.applicationform.exception;

public class AlreadyRequestJoinClubException extends RuntimeException {
    private static final String MESSAGE = "이미 가입 신청한 모임입니다.";

    public AlreadyRequestJoinClubException() {
        super(MESSAGE);
    }
}
