package com.mohaeng.application.club.service;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.domain.club.domain.ClubCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CreateClub 은 ")
class CreateClubTest {

    private final ClubCommand clubCommand = mock(ClubCommand.class);
    private final CreateClubUseCase createClubUseCase = new CreateClub(clubCommand);

    @Test
    @DisplayName("모임 이름, 모임 설명, 최대 인원을 가지고 모임을 생성한다.")
    void test() {
        when(clubCommand.save(any(Club.class)))
                .thenReturn(1L);

        String name = "sample name";
        String description = "sample description";
        int maxPeopleCount = 100;
        Long clubId = createClubUseCase.command(
                new CreateClubUseCase.Command(name, description, maxPeopleCount)
        );

        assertAll(
                () -> assertThat(clubId).isEqualTo(1L),
                () -> verify(clubCommand, times(1)).save(any(Club.class))
        );
    }
}