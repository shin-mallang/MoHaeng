package com.mohaeng.club.exception;

public class ClubFullException extends RuntimeException {
    private static final String MESSAGE = "모임의 회원 수가 가득 차 더 이상 회원을 받을 수 없습니다.";

    public ClubFullException() {
        super(MESSAGE);
    }
}
