package com.mohaeng.club.application.mapper;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.domain.model.Club;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.ClubFixture.createClubUseCaseCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClubApplicationMapper 는 ")
class ClubApplicationMapperTest {

    @Test
    @DisplayName("CreateClubUseCase.Command 를 Club 으로 변환한다.")
    void test() {
        // given
        CreateClubUseCase.Command clubUseCaseCommand = createClubUseCaseCommand(1L);

        // when
        Club club = ClubApplicationMapper.toDomainEntity(clubUseCaseCommand);

        // then
        assertAll(
                () -> assertThat(clubUseCaseCommand.name()).isEqualTo(club.name()),
                () -> assertThat(clubUseCaseCommand.description()).isEqualTo(club.description()),
                () -> assertThat(clubUseCaseCommand.maxPeopleCount()).isEqualTo(club.maxParticipantCount()),
                () -> assertThat(club.currentParticipantCount()).isEqualTo(0)
        );
    }
}