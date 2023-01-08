package com.mohaeng.common.exception;

public abstract class BaseException extends RuntimeException{

    public BaseException() {
    }

    public BaseException(final Throwable cause) {
        super(cause);
    }

    public abstract BaseExceptionType exceptionType();
}