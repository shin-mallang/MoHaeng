package com.mohaeng.authentication.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthenticationExceptionType implements BaseExceptionType {

    INCORRECT_AUTHENTICATION(200, HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않아 로그인에 실패하였습니다."),
    NOT_FOUND_ACCESS_TOKEN(201, HttpStatus.UNAUTHORIZED, "AccessToken이 존재하지 않습니다."),
    INVALID_ACCESS_TOKEN(201, HttpStatus.UNAUTHORIZED, "AccessToken이 유효하지 않습니다."),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    AuthenticationExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
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
