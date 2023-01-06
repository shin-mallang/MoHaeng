package com.mohaeng.member.exception;

import static java.lang.String.format;

public class NotFoundMemberException extends RuntimeException {

    private static final String MESSAGE = "회원이 존재하지 않습니다.";
    private static final String MESSAGE_FORMAT = "회원이 존재하지 않습니다. (id = %d)";

    public NotFoundMemberException() {
        super(MESSAGE);
    }

    public NotFoundMemberException(final Long memberId) {
        super(format(MESSAGE_FORMAT, memberId));
    }
}
