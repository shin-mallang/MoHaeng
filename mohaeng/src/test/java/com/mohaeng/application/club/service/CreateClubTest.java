package com.mohaeng.application.club.service;

import com.mohaeng.application.club.MockClubRepository;
import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.common.event.Event;
import com.mohaeng.domain.club.event.club.CreateClubEvent;
import com.mohaeng.domain.club.repository.club.ClubRepository;
import com.mohaeng.domain.member.model.Member;
import com.mohaeng.domain.member.model.enums.Gender;
import com.mohaeng.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

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
        when(memberRepository.findById(any())).thenReturn(Optional.of(new Member(1L, LocalDateTime.now(), LocalDateTime.now(),
                "username", "password", "name", 10, Gender.MAN)));
    }

    @Test
    @DisplayName("회원 id, 모임 이름, 모임 설명, 최대 인원수를 가지고 모임을 생성한다.")
    void createTest() {
        // given
        final Long memberId = 1L;
        final String name = "name";
        final String description = "description";
        final int maxPeopleCount = 10;

        // when
        Long clubId = clubUseCase.command(
                new CreateClubUseCase.Command(memberId, name, description, maxPeopleCount)
        );

        // then
        assertAll(() -> assertThat(clubId).isNotNull());
    }

    @Test
    @DisplayName("모임을 생성시 이벤트를 발행한다.")
    void publishEventTest() {
        // given
        Event.setApplicationEventPublisher(applicationEventPublisher);
        final Long memberId = 1L;
        final String name = "name";
        final String description = "description";
        final int maxPeopleCount = 10;

        // when
        Long clubId = clubUseCase.command(
                new CreateClubUseCase.Command(memberId, name, description, maxPeopleCount)
        );

        // then
        assertAll(
                () -> assertThat(clubId).isNotNull(),
                () -> verify(applicationEventPublisher, times(1)).publishEvent(any(CreateClubEvent.class))
        );
    }
}