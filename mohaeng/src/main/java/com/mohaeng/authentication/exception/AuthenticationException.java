package com.mohaeng.authentication.exception;

import com.mohaeng.club.exception.ClubExceptionType;
import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class AuthenticationException extends BaseException {

    private final AuthenticationExceptionType exceptionType;

    public AuthenticationException(final AuthenticationExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public AuthenticationException(final AuthenticationExceptionType exceptionType, final Throwable cause) {
        super(cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
