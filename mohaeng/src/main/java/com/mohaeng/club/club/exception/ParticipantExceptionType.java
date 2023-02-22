package com.mohaeng.club.club.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ParticipantExceptionType implements BaseExceptionType {

    NOT_FOUND_PARTICIPANT(600, HttpStatus.NOT_FOUND, "참여자가 존재하지 않습니다."),
    ALREADY_EXIST_PARTICIPANT(601, HttpStatus.CONFLICT, "이미 가입된 참여자입니다."),
    PRESIDENT_CAN_NOT_LEAVE_CLUB(602, HttpStatus.BAD_REQUEST, "회장은 모임을 탈퇴할 수 없습니다."),
    NO_AUTHORITY_EXPEL_PARTICIPANT(603, HttpStatus.FORBIDDEN, "참여자를 추방시킬 권한이 없습니다.(같은 모임의 회장만이 가능합니다)"),
    NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE(604, HttpStatus.FORBIDDEN, "참여자의 역할을 변경할 권한이 없습니다.(같은 모임의 회장만이 가능합니다)"),
    NOT_CHANGE_PRESIDENT_ROLE(605, HttpStatus.BAD_REQUEST, "회장의 역할로 변경할수는 없습니다."),
    NO_AUTHORITY_DELEGATE_PRESIDENT(606, HttpStatus.FORBIDDEN, "회장만이 회장의 역할을 위임할 수 있습니다."),
    NOT_PRESIDENT(698, HttpStatus.INTERNAL_SERVER_ERROR, "회장이 아닙니다."),
    NOT_FOUND_PRESIDENT(699, HttpStatus.INTERNAL_SERVER_ERROR, "회장이 존재하지 않습니다."),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ParticipantExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
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