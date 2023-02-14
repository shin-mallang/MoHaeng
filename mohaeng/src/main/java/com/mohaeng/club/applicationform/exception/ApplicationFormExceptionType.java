package com.mohaeng.club.applicationform.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ApplicationFormExceptionType implements BaseExceptionType {

    ALREADY_PROCESSED(700, HttpStatus.BAD_REQUEST, "이미 처리된 가입신청서입니다."),
    NO_AUTHORITY_PROCESS_APPLICATION(701, HttpStatus.FORBIDDEN, "가입 신청서를 처리할 권한이 없습니다."),
    NOT_FOUND_APPLICATION_FORM(702, HttpStatus.NOT_FOUND, "가입 신청서를 찾을 수 없습니다."),
    ;

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