package com.mohaeng.club.club.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ParticipantExceptionType implements BaseExceptionType {

    NOT_FOUND_PARTICIPANT(600, HttpStatus.NOT_FOUND, "참여자가 존재하지 않습니다."),
    ALREADY_EXIST_PARTICIPANT(601, HttpStatus.CONFLICT, "이미 가입된 참여자입니다."),
    NOT_PRESIDENT(698, HttpStatus.INTERNAL_SERVER_ERROR, "회장이 아닙니다."),
    NOT_FOUND_PRESIDENT(699, HttpStatus.INTERNAL_SERVER_ERROR, "회장이 존재하지 않습니다."),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ParticipantExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int errorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}