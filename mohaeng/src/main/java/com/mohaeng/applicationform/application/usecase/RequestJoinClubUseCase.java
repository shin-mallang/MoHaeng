package com.mohaeng.applicationform.application.usecase;

public interface RequestJoinClubUseCase {

    /**
     * 모임에 가입을 신청한다.
     * 생성된 가입 신청서의 ID가 반환된다.
     */
    Long command(final Command command);

    record Command(
            Long applicantId,
            Long targetClubId
    ) {
    }
}
