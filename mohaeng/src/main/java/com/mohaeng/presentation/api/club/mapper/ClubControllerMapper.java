package com.mohaeng.presentation.api.club.mapper;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.presentation.api.club.CreateClubRestController;

public class ClubControllerMapper {

    public static CreateClubUseCase.Command toApplicationDto(final CreateClubRestController.CreateClubRequest request) {
        return null;
    }
}
