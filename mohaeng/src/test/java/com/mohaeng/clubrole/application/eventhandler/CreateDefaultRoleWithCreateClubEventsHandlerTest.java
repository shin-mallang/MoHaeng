package com.mohaeng.clubrole.application.eventhandler;

import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.club.domain.event.CreateClubEventHistory;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.common.event.Events;
import com.mohaeng.common.fixtures.MemberFixture;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.clubRolesWithId;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("CreateDefaultRoleWithCreateClubEventHandler 는 ")
class CreateDefaultRoleWithCreateClubEventsHandlerTest {

    private final EventHistoryRepository eventHistoryRepository = mock(EventHistoryRepository.class);
    private final ClubRoleRepository clubRoleRepository = mock(ClubRoleRepository.class);
    private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

    private final CreateDefaultRoleWithCreateClubEventHandler eventHandler =
            new CreateDefaultRoleWithCreateClubEventHandler(eventHistoryRepository, clubRoleRepository);

    @Test
    @DisplayName("클럽 생성 이벤트(CreateClubEvent) 를 받으면 기본 역할을 생성한 후 처리된 이벤트를 저장한다.")
    void createDefaultRole() {
        // given
        final Member member = MemberFixture.member(1L);
        final Club club = club(1L);
        CreateClubEvent createClubEvent = new CreateClubEvent(this, member, club);
        List<ClubRole> clubRoles = clubRolesWithId(club);
        when(clubRoleRepository.saveAll(any())).thenReturn(clubRoles);

        // when
        eventHandler.handle(createClubEvent);

        // then
        assertAll(
                () -> verify(clubRoleRepository, times(1)).saveAll(any()),
                () -> verify(eventHistoryRepository, times(1)).save(any(CreateClubEventHistory.class))
        );
    }

    @Test
    @DisplayName("기본 역할을 생성한 이후 기본 역할 생성 이벤트를 발행한다.")
    void publishCreateDefaultRoleEvent() {
        // given
        Events.setApplicationEventPublisher(applicationEventPublisher);
        final Member member = MemberFixture.member(1L);
        Club club = club(1L);
        CreateClubEvent createClubEvent = new CreateClubEvent(this, member, club);
        List<ClubRole> clubRoles = clubRolesWithId(club);
        when(clubRoleRepository.saveAll(any())).thenReturn(clubRoles);

        // when
        eventHandler.handle(createClubEvent);

        // then
        assertAll(
                () -> verify(clubRoleRepository, times(1)).saveAll(any()),
                () -> verify(eventHistoryRepository, times(1)).save(any(CreateClubEventHistory.class)),
                () -> verify(applicationEventPublisher, times(1)).publishEvent(any(CreateDefaultRoleEvent.class))
        );
    }
}