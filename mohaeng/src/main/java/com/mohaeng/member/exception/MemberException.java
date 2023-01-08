package com.mohaeng.member.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class MemberException extends BaseException {

    private final MemberExceptionType exceptionType;

    public MemberException(final MemberExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public MemberException(final MemberExceptionType exceptionType, final Throwable cause) {
        super(cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
