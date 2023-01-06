package com.mohaeng.applicationform.exception;

public class AlreadyJoinedMemberException extends RuntimeException {
    private static final String MESSAGE = "이미 모임에 가입된 회원은 같은 모임에 가입 신청을 보낼 수 없습니다.";

    public AlreadyJoinedMemberException() {
        super(MESSAGE);
    }
}
