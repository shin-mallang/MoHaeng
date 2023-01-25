package com.mohaeng.participant.exception;

import com.mohaeng.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ParticipantExceptionType implements BaseExceptionType {

    NOT_FOUND_PARTICIPANT(600, HttpStatus.NOT_FOUND, "참여자가 존재하지 않습니다."),
    NOT_FOUND_PRESIDENT(601, HttpStatus.NOT_FOUND, "주어진 모임의 회장이 없습니다. (발생하면 안되는 오류입니다.)"),
    PRESIDENT_CAN_NOT_LEAVE_CLUB(602, HttpStatus.BAD_REQUEST, "모임의 회장은 모임에서 탈퇴할 수 없습니다."),
    MISMATCH_BETWEEN_PARTICIPANT_AND_MEMBER(603, HttpStatus.FORBIDDEN, "현재 요청한 회원과, 참여자가 일치하지 않아 거부되었습니다."),
    NO_AUTHORITY_EXPEL_PARTICIPANT(604, HttpStatus.FORBIDDEN, "회원을 모임에서 추방시킬 권한이 존재하지 않습니다. (회장만이 추방 가능합니다.)"),
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
