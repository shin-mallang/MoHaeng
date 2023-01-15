package com.mohaeng.alarm.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class AlarmException extends BaseException {

    private final AlarmExceptionType exceptionType;

    public AlarmException(final AlarmExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public AlarmException(final AlarmExceptionType exceptionType, final Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
