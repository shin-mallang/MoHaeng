package com.mohaeng.club.club.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ClubRoleExceptionType implements BaseExceptionType {

    NOT_FOUND_DEFAULT_ROLE(400, HttpStatus.INTERNAL_SERVER_ERROR, "기본 역할을 찾을 수 없습니다. (발생하면 안됨)"),
    NOT_FOUND_ROLE(401, HttpStatus.NOT_FOUND, "역할을 찾을 수 없습니다."),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ClubRoleExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
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