package com.mohaeng.presentation.api.club.mapper;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.presentation.api.club.CreateClubController;

public class ClubControllerMapper {

    public static CreateClubUseCase.Command toApplicationDto(final Long memberId,
                                                             final CreateClubController.CreateClubRequest request) {
        return new CreateClubUseCase.Command(
                memberId,
                request.name(),
                request.description(),
                request.maxPeopleCount());
    }
}
