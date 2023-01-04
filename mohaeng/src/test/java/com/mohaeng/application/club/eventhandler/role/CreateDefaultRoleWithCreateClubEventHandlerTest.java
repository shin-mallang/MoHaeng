package com.mohaeng.application.club.eventhandler.role;

import com.mohaeng.common.event.Event;
import com.mohaeng.domain.club.event.club.CreateClubEvent;
import com.mohaeng.domain.club.event.club.CreateClubEventHistory;
import com.mohaeng.domain.club.event.role.CreateDefaultRoleEvent;
import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.club.model.role.ClubRole;
import com.mohaeng.domain.club.repository.role.ClubRoleRepository;
import com.mohaeng.domain.config.event.EventHistoryRepository;
import com.mohaeng.domain.member.model.Member;
import com.mohaeng.domain.member.model.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("CreateDefaultRoleWithCreateClubEventHandler 는 ")
class CreateDefaultRoleWithCreateClubEventHandlerTest {

    private final EventHistoryRepository eventHistoryRepository = mock(EventHistoryRepository.class);
    private final ClubRoleRepository clubRoleRepository = mock(ClubRoleRepository.class);
    private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

    private final CreateDefaultRoleWithCreateClubEventHandler eventHandler =
            new CreateDefaultRoleWithCreateClubEventHandler(eventHistoryRepository, clubRoleRepository);

    @Test
    @DisplayName("클럽 생성 이벤트(CreateClubEvent) 를 받으면 기본 역할을 생성한 후 처리된 이벤트를 저장한다.")
    void createDefaultRole() {
        // given
        final Member member = new Member(1L, LocalDateTime.now(), LocalDateTime.now(),
                "username", "password", "name", 10, Gender.MAN);
        final Club club = new Club(1L, LocalDateTime.now(), LocalDateTime.now(), "name", "des", 100);
        CreateClubEvent createClubEvent = new CreateClubEvent(this, member, club);
        List<ClubRole> clubRoles = ClubRole.defaultRoles(club);
        clubRoles.forEach(it -> ReflectionTestUtils.setField(it, "id", 1L));
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
        Event.setApplicationEventPublisher(applicationEventPublisher);
        final Member member = new Member(1L, LocalDateTime.now(), LocalDateTime.now(),
                "username", "password", "name", 10, Gender.MAN);
        final Club club = new Club(1L, LocalDateTime.now(), LocalDateTime.now(), "name", "des", 100);
        CreateClubEvent createClubEvent = new CreateClubEvent(this, member, club);
        List<ClubRole> clubRoles = ClubRole.defaultRoles(club);
        clubRoles.forEach(it -> ReflectionTestUtils.setField(it, "id", 1L));
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