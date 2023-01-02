package com.mohaeng.application.club.service;

import com.mohaeng.application.club.MockClubRepository;
import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.domain.club.ClubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CreateClub은 ")
class CreateClubTest {

    private final ClubRepository clubRepository = new MockClubRepository();
    private final CreateClubUseCase clubUseCase = new CreateClub(clubRepository);

    @Test
    @DisplayName("모임 이름, 모임 설명, 최대 인원수를 가지고 생성된다.")
    void createTest() {
        // given
        final String name = "name";
        final String description = "description";
        final int maxPeopleCount = 10;

        // when
        Long clubId = clubUseCase.command(
                new CreateClubUseCase.Command(name, description, maxPeopleCount)
        );

        // then
        assertAll(() -> assertThat(clubId).isNotNull());
    }
}