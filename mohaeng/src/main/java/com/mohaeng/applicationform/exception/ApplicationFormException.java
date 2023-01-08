package com.mohaeng.applicationform.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class ApplicationFormException extends BaseException {

    private final ApplicationFormExceptionType exceptionType;

    public ApplicationFormException(final ApplicationFormExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ApplicationFormException(final ApplicationFormExceptionType exceptionType, final Throwable cause) {
        super(cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
