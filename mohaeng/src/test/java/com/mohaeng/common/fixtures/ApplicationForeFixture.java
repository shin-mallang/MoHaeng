package com.mohaeng.common.fixtures;

import com.mohaeng.applicationform.application.usecase.RequestJoinClubUseCase;

public class ApplicationForeFixture {

    public static RequestJoinClubUseCase.Command requestJoinClubUseCaseCommand(final Long applicantId, final Long targetClubId) {
        return new RequestJoinClubUseCase.Command(applicantId, targetClubId);
    }
}
