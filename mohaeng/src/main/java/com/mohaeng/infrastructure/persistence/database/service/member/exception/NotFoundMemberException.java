package com.mohaeng.infrastructure.persistence.database.service.member.exception;

public class NotFoundMemberException extends RuntimeException {

    private static final String MESSAGE = "찾으시는 회원이 없습니다.";

    public NotFoundMemberException() {
        super(MESSAGE);
    }
}
