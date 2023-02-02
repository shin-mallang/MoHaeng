package com.mohaeng.participant.application.eventhandler;

import com.mohaeng.club.domain.event.DeleteClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.event.DeleteParticipantEvent;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("DeleteParticipantAndClubRoleWithDeleteClubEventHandler 는 ")
class DeleteParticipantAndClubRoleWithDeleteClubEventHandlerTest extends EventHandlerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ApplicationEvents events;

    private DeleteParticipantAndClubRoleWithDeleteClubEventHandler handler;

    @BeforeEach
    public void init() {
        handler = new DeleteParticipantAndClubRoleWithDeleteClubEventHandler(eventHistoryRepository, participantRepository, clubRoleRepository);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("DeleteClubEvent를 받으면 참여자와 기본 역할을 모두 제거한다.")
        void success_test_1() {
            // given
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            flushAndClear();

            assertAll(
                    () -> assertThat(em.createQuery("select p from Participant p", Participant.class)
                            .getResultList().size())
                            .isEqualTo(5),
                    () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class)
                            .getResultList().size())
                            .isEqualTo(3)
            );

            // when
            handler.handle(new DeleteClubEvent(this, club.id(), club.name(), club.description()));
            flushAndClear();

            // then
            assertAll(
                    () -> assertThat(em.createQuery("select p from Participant p", Participant.class)
                            .getResultList().size())
                            .isEqualTo(0),
                    () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class)
                            .getResultList().size())
                            .isEqualTo(0)
            );
        }

        @Test
        @DisplayName("참여자와 모임의 기본 역할을 모두 제거한 뒤 참여자 제거 이벤트를 발행한다.")
        void success_test_2() {
            // given
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            flushAndClear();

            // when
            handler.handle(new DeleteClubEvent(this, club.id(), club.name(), club.description()));

            // then
            assertAll(
                    () -> assertThat(em.createQuery("select p from Participant p", Participant.class)
                            .getResultList().size())
                            .isEqualTo(0),
                    () -> assertThat(events.stream(DeleteParticipantEvent.class).count()).isEqualTo(1L)
            );
        }
    }

    private Participant saveParticipant(final Member member, final Club club, final ClubRole clubRole) {
        return participantRepository.save(participant(null, member, club, clubRole));
    }

    private Map<ClubRoleCategory, ClubRole> saveDefaultClubRoles(final Club club) {
        return clubRoleRepository.saveAll(ClubRole.defaultRoles(club))
                .stream()
                .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));
    }

    private Club saveClub() {
        return clubRepository.save(club(null));
    }

    private Member saveMember() {
        return memberRepository.save(member(null));
    }
}