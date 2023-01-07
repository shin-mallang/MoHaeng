package com.mohaeng.applicationform.exception;

public class AlreadyProcessedApplicationFormException extends RuntimeException {
    private static final String MESSAGE = "이미 처리한 가입 신청서는 또다시 처리될 수 없습니다.";

    public AlreadyProcessedApplicationFormException() {
        super(MESSAGE);
    }
}
