package com.mohaeng.common.exception;

public abstract class BaseException extends RuntimeException{

    public abstract BaseExceptionType exceptionType();
}