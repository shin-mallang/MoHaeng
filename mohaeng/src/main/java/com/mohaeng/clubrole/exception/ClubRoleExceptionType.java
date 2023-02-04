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
    ALREADY_DEFAULT_ROLE(406, HttpStatus.BAD_REQUEST, "이미 기본 역할로 설정되어 있습니다."),
    NO_AUTHORITY_CHANGE_DEFAULT_ROLE(407, HttpStatus.FORBIDDEN, "기본 역할을 변경할 권한이 없습니다."),
    MISMATCH_EXISTING_DEFAULT_ROLE_AND_CANDIDATE(408, HttpStatus.BAD_REQUEST, "기존에 존재하였던 기본 역할과, 기본 역할로 바꾸려는 역할의 카테고리가 일치하지 않습니다."),
    CAN_NOT_COMPARE_OTHER_CLUB_ROLE(409, HttpStatus.BAD_REQUEST, "다른 모임의 역할과는 비교할 수 없습니다."),
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