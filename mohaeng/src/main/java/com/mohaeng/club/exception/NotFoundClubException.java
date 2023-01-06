package com.mohaeng.club.exception;

import static java.lang.String.format;

public class NotFoundClubException extends RuntimeException {
    private static final String MESSAGE_FORMAT = "찾으시는 모임이 없습니다 (id = %d)";

    public NotFoundClubException(final Long id) {
        super(format(MESSAGE_FORMAT, id));
    }
}
