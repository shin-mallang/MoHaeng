package com.mohaeng.clubrole.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ClubRoleExceptionType implements BaseExceptionType {

    NOT_FOUND_CLUB_ROLE(400, HttpStatus.NOT_FOUND, "찾으시는 역할이 없습니다."),
    NO_AUTHORITY_CREATE_ROLE(401, HttpStatus.FORBIDDEN, "모임의 역할을 생성할 권한이 없습니다."),
    CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE(402, HttpStatus.BAD_REQUEST, "회장 역할은 추가적으로 생성할 수 없습니다."),
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