package com.mohaeng.clubrole.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ClubRoleExceptionType implements BaseExceptionType {

    NOT_FOUND_CLUB_ROLE(400, HttpStatus.NOT_FOUND, "찾으시는 역할이 없습니다."),
    NO_AUTHORITY_CREATE_ROLE(401, HttpStatus.FORBIDDEN, "모임의 역할을 생성할 권한이 없습니다."),
    CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE(402, HttpStatus.BAD_REQUEST, "회장 역할은 추가적으로 생성할 수 없습니다."),
    NO_AUTHORITY_CHANGE_ROLE_NAME(403, HttpStatus.FORBIDDEN, "역할의 이름을 변경할 권한이 없습니다."),
    NO_AUTHORITY_DELETE_ROLE(404, HttpStatus.FORBIDDEN, "역할을 제거할 권한이 없습니다."),
    CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE(405, HttpStatus.BAD_REQUEST, "해당 범주에 속하는 다른 역할이 존재하지 않아 해당 역할을 제거할 수 없습니다."),
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