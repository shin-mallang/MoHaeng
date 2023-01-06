package com.mohaeng.club.presentation.mapper;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.presentation.CreateClubController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.ClubFixture.createClubRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ClubControllerMapper 는 ")
class ClubControllerMapperTest {

    @Test
    @DisplayName("회원 id와 CreateClubRequest를 CreateClubUseCase.Command 로 반환한다.")
    void test() {
        // given
        CreateClubController.CreateClubRequest clubRequest = createClubRequest("name", "des", 10);
        Long memberId = 1L;

        // when
        CreateClubUseCase.Command command = ClubControllerMapper.toApplicationDto(memberId, clubRequest);

        // then
        assertAll(
                () -> assertThat(command.name()).isEqualTo(clubRequest.name()),
                () -> assertThat(command.description()).isEqualTo(clubRequest.description()),
                () -> assertThat(command.maxPeopleCount()).isEqualTo(clubRequest.maxParticipantCount()),
                () -> assertThat(command.memberId()).isEqualTo(memberId)
        );
    }
}