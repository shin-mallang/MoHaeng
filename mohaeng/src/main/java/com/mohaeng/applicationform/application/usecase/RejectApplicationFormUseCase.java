package com.mohaeng.applicationform.application.usecase;

public interface RejectApplicationFormUseCase {

    /**
     * 모임 가입 요청 거절
     */
    void command(final Command command);

    record Command(
            Long applicationFormId,  // 가입 신청서 ID
            Long managerId  // 처리한 Member ID
    ) {
    }
}
