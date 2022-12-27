package com.mohaeng.infrastructure.persistence.database.service.club;

import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.domain.club.domain.ClubCommand;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.club.ClubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("ClubJpaCommand 는 ")
class ClubJpaCommandTest {

    private final ClubRepository clubRepository = mock(ClubRepository.class);
    private final ClubCommand clubCommand = new ClubJpaCommand(clubRepository);

    @Test
    @DisplayName("save() 시 ClubRepository의 save()를 호출한다.")
    void save() {
        ClubJpaEntity mock = mock(ClubJpaEntity.class);
        when(clubRepository.save(any(ClubJpaEntity.class))).thenReturn(mock);
        when(mock.id()).thenReturn(1L);

        clubCommand.save(mock(Club.class));

        verify(clubRepository, times(1))
                .save(any(ClubJpaEntity.class));
    }
}