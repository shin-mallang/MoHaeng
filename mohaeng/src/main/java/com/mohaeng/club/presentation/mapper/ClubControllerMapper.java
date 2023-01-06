package com.mohaeng.club.presentation.mapper;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.presentation.CreateClubController;

public class ClubControllerMapper {

    public static CreateClubUseCase.Command toApplicationDto(final Long memberId,
                                                             final CreateClubController.CreateClubRequest request) {
        return new CreateClubUseCase.Command(
                memberId,
                request.name(),
                request.description(),
                request.maxParticipantCount());
    }
}
