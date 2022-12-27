package com.mohaeng.presentation.api.club.mapper;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.presentation.api.club.request.CreateClubRequest;

public class ClubControllerMapper {

    public static CreateClubUseCase.Command toApplicationLayerDto(
            final Long memberId,
            final CreateClubRequest createClubRequest
    ) {
        return new CreateClubUseCase.Command(
                memberId,
                createClubRequest.name(),
                createClubRequest.description(),
                createClubRequest.maxPeopleCount()
        );
    }
}
