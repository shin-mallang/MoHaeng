package com.mohaeng.club.applicationform.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class ApplicationFormException extends BaseException {

    private final ApplicationFormExceptionType exceptionType;

    public ApplicationFormException(final ApplicationFormExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public ApplicationFormException(final ApplicationFormExceptionType exceptionType, final Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
