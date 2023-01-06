package com.mohaeng.common.fixtures;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.presentation.CreateClubController;
import org.springframework.test.util.ReflectionTestUtils;

public class ClubFixture {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "des";
    public static final int MAX_PEOPLE_COUNT = 100;

    public static Club club(final Long clubId) {
        Club club = new Club(NAME, DESCRIPTION, MAX_PEOPLE_COUNT);
        ReflectionTestUtils.setField(club, "id", clubId);
        return club;
    }

    public static CreateClubUseCase.Command createClubUseCaseCommand(Long id) {
        return new CreateClubUseCase.Command(id, NAME, DESCRIPTION, MAX_PEOPLE_COUNT);
    }

    public static CreateClubController.CreateClubRequest createClubRequest(String name, String des, int maxParticipantCount) {
        return new CreateClubController.CreateClubRequest(name, des, maxParticipantCount);
    }
}
