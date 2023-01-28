package com.mohaeng.club.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ClubExceptionType implements BaseExceptionType {

    CLUB_IS_FULL(300, HttpStatus.BAD_REQUEST, "모임의 회원 수가 가득 차 더 이상 회원을 받을 수 없습니다."),
    NOT_FOUND_CLUB(301, HttpStatus.NOT_FOUND, "찾으시는 모임이 없습니다."),
    CLUB_IS_EMPTY(302, HttpStatus.BAD_REQUEST, "모임의 인원은 0명이 될 수 없습니다."),
    NO_AUTHORITY_DELETE_CLUB(303, HttpStatus.FORBIDDEN, "모임을 제거할 권한이 없습니다.");

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
