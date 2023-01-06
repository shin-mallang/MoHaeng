package com.mohaeng.club.application.service;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.common.event.Events;
import com.mohaeng.common.fixtures.MemberFixture;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static com.mohaeng.common.fixtures.ClubFixture.createClubUseCaseCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("CreateClub은 ")
class CreateClubTest {

    private final ClubRepository clubRepository = new MockClubRepository();
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final CreateClubUseCase clubUseCase = new CreateClub(clubRepository, memberRepository);
    private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

    @BeforeEach
    public void init() {
        when(memberRepository.findById(any())).thenReturn(Optional.of(MemberFixture.member(1L)));
    }

    @Test
    @DisplayName("회원 id, 모임 이름, 모임 설명, 최대 인원수를 가지고 모임을 생성한다.")
    void createTest() {
        // when
        Long clubId = clubUseCase.command(createClubUseCaseCommand());

        // then
        assertAll(() -> assertThat(clubId).isNotNull());
    }

    @Test
    @DisplayName("모임을 생성시 이벤트를 발행한다.")
    void publishEventTest() {
        // given
        Events.setApplicationEventPublisher(applicationEventPublisher);

        // when
        Long clubId = clubUseCase.command(createClubUseCaseCommand());

        // then
        assertAll(
                () -> assertThat(clubId).isNotNull(),
                () -> verify(applicationEventPublisher, times(1)).publishEvent(any(CreateClubEvent.class))
        );
    }
}