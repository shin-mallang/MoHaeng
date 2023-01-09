package com.mohaeng.clubrole.application.eventhandler;

import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.common.event.Events;
import com.mohaeng.common.repositories.MockClubRepository;
import com.mohaeng.common.repositories.MockClubRoleRepository;
import com.mohaeng.common.repositories.MockMemberRepository;
import com.mohaeng.member.domain.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.clubRolesWithId;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("CreateDefaultRoleWithCreateClubEventHandler 는 ")
class CreateDefaultRoleWithCreateClubEventsHandlerTest extends EventHandlerTest {

    private final MockMemberRepository memberRepository = new MockMemberRepository();
    private final MockClubRepository clubRepository = new MockClubRepository();
    private final MockClubRoleRepository clubRoleRepository = new MockClubRoleRepository();
    private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

    private final CreateDefaultRoleWithCreateClubEventHandler eventHandler =
            new CreateDefaultRoleWithCreateClubEventHandler(eventHistoryRepository, clubRepository, clubRoleRepository);

    @Test
    @DisplayName("클럽 생성 이벤트(CreateClubEvent) 를 받으면 기본 역할을 생성한다.")
    void createDefaultRole() {
        // given
        final Member member = memberRepository.save(member(null));
        final Club club = clubRepository.save(club(null));

        CreateClubEvent createClubEvent = new CreateClubEvent(this, member.id(), club.id());
        List<ClubRole> clubRoles = clubRolesWithId(club);

        // when
        eventHandler.handle(createClubEvent);

        // then
        assertAll(
                () -> Assertions.assertThat(clubRoleRepository.findAll().size()).isEqualTo(clubRoles.size())
        );
    }

    @Test
    @DisplayName("기본 역할을 생성한 이후 기본 역할 생성 이벤트를 발행한다.")
    void publishCreateDefaultRoleEvent() {
        // given
        Events.setApplicationEventPublisher(applicationEventPublisher);
        final Member member = memberRepository.save(member(null));
        final Club club = clubRepository.save(club(null));

        CreateClubEvent createClubEvent = new CreateClubEvent(this, member.id(), club.id());
        List<ClubRole> clubRoles = clubRolesWithId(club);

        // when
        eventHandler.handle(createClubEvent);

        // then
        assertAll(
                () -> Assertions.assertThat(clubRoleRepository.findAll().size()).isEqualTo(clubRoles.size()),
                () -> verify(applicationEventPublisher, times(1)).publishEvent(any(CreateDefaultRoleEvent.class))
        );
    }
}