package com.mohaeng.club.club.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ClubExceptionType implements BaseExceptionType {

    CLUB_IS_FULL(300, HttpStatus.BAD_REQUEST, "모임이 가득 차 더이상 참여자를 받을 수 없습니다."),
    NOT_FOUND_CLUB(301, HttpStatus.NOT_FOUND, "모임을 찾을 수 없습니다."),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ClubExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
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