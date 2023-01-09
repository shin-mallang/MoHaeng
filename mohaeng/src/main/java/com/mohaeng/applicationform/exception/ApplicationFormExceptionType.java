package com.mohaeng.applicationform.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ApplicationFormExceptionType implements BaseExceptionType {

    ALREADY_MEMBER_JOINED_CLUB(400, HttpStatus.BAD_REQUEST, "이미 모임에 가입된 회원은 같은 모임에 가입 신청을 보낼 수 없습니다."),
    ALREADY_PROCESSED_APPLICATION_FORM(400, HttpStatus.BAD_REQUEST, "이미 처리한 가입 신청서는 또다시 처리될 수 없습니다."),
    ALREADY_REQUEST_JOIN_CLUB(400, HttpStatus.BAD_REQUEST, "이미 가입 신청한 모임입니다."),
    NOT_FOUND_APPLICATION_FORM(404, HttpStatus.NOT_FOUND, "가입 신청 내역을 찾을 수 없습니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ApplicationFormExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
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
