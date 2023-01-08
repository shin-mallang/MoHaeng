package com.mohaeng.clubrole.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class ClubRoleException extends BaseException {

    private final ClubRoleExceptionType exceptionType;

    public ClubRoleException(final ClubRoleExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ClubRoleException(final ClubRoleExceptionType exceptionType, final Throwable cause) {
        super(cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
