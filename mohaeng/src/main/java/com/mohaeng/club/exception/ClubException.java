package com.mohaeng.club.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class ClubException extends BaseException {

    private final ClubExceptionType exceptionType;

    public ClubException(final ClubExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ClubException(final ClubExceptionType exceptionType, final Throwable cause) {
        super(cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
