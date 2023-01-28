package com.mohaeng.clubrole.application.eventhandler;

import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import java.util.List;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.clubRolesWithId;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CreateDefaultRoleWithCreateClubEventHandler 는 ")
class CreateDefaultRoleWithCreateClubEventsHandlerTest extends EventHandlerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ApplicationEvents events;

    private CreateDefaultRoleWithCreateClubEventHandler eventHandler;

    @BeforeEach
    public void init() {
        eventHandler = new CreateDefaultRoleWithCreateClubEventHandler(eventHistoryRepository, clubRepository, clubRoleRepository);
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("클럽 생성 이벤트(CreateClubEvent) 를 받으면 기본 역할을 생성한다.")
        void success_test_1() {
            // given
            final Member member = memberRepository.save(member(null));
            final Club club = clubRepository.save(club(null));

            CreateClubEvent createClubEvent = new CreateClubEvent(this, member.id(), club.id());
            List<ClubRole> clubRoles = clubRolesWithId(club);

            // when
            eventHandler.handle(createClubEvent);

            // then
            assertAll(
                    () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class).getResultList().size()).isEqualTo(clubRoles.size())
            );
        }

        @Test
        @DisplayName("기본 역할을 생성한 이후 기본 역할 생성 이벤트를 발행한다.")
        void success_test_2() {
            // given
            final Member member = memberRepository.save(member(null));
            final Club club = clubRepository.save(club(null));

            CreateClubEvent createClubEvent = new CreateClubEvent(this, member.id(), club.id());
            List<ClubRole> clubRoles = clubRolesWithId(club);

            // when
            eventHandler.handle(createClubEvent);

            // then
            assertAll(
                    () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class).getResultList().size()).isEqualTo(clubRoles.size()),
                    () -> assertThat(events.stream(CreateDefaultRoleEvent.class).count()).isEqualTo(1L)
            );
        }
    }
}

