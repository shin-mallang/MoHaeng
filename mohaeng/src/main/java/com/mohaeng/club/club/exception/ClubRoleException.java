package com.mohaeng.club.club.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class ClubRoleException extends BaseException {

    private final ClubRoleExceptionType exceptionType;

    public ClubRoleException(final ClubRoleExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public ClubRoleException(final ClubRoleExceptionType exceptionType, final Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}