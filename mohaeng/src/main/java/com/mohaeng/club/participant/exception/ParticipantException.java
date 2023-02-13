package com.mohaeng.club.participant.exception;

import com.mohaeng.common.exception.BaseException;
import com.mohaeng.common.exception.BaseExceptionType;

public class ParticipantException extends BaseException {

    private final ParticipantExceptionType exceptionType;

    public ParticipantException(final ParticipantExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public ParticipantException(final ParticipantExceptionType exceptionType, final Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}