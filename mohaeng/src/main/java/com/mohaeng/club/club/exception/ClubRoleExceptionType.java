package com.mohaeng.club.club.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ClubRoleExceptionType implements BaseExceptionType {

    NOT_FOUND_DEFAULT_ROLE(400, HttpStatus.INTERNAL_SERVER_ERROR, "기본 역할을 찾을 수 없습니다. (발생하면 안됨)"),
    NOT_FOUND_ROLE(401, HttpStatus.NOT_FOUND, "역할을 찾을 수 없습니다."),
    NO_AUTHORITY_CREATE_ROLE(402, HttpStatus.FORBIDDEN, "역할을 생설할 권한이 없습니다."),
    CAN_NOT_CREATE_PRESIDENT_ROLE(403, HttpStatus.BAD_REQUEST, "회장 역할을 새로 생성할 수 없습니다."),
    DUPLICATED_NAME(404, HttpStatus.CONFLICT, "중복되는 이름입니다."),
    NO_AUTHORITY_CHANGE_ROLE_NAME(405, HttpStatus.FORBIDDEN, "역할 이름을 바꿀 권한이 없습니다"),
    NO_AUTHORITY_DELETE_ROLE(406, HttpStatus.FORBIDDEN, "역할을 제거할 권한이 없습니다"),
    CAN_NOT_DELETE_DEFAULT_ROLE(407, HttpStatus.BAD_REQUEST, "기본 역할은 제거할 수 없습니다"),
    NO_AUTHORITY_CHANGE_DEFAULT_ROLE(408, HttpStatus.FORBIDDEN, "기본 역할을 바꿀 권한이 없습니다"),
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