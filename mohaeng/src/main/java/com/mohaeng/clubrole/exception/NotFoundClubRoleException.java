package com.mohaeng.clubrole.exception;

import static java.lang.String.format;

public class NotFoundClubRoleException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "찾으시는 역할이 없습니다. (id = %d)";

    public NotFoundClubRoleException(Long id) {
        super(format(MESSAGE_FORMAT, id));
    }
}
