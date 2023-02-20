package com.mohaeng.club.club.application.service;

import com.mohaeng.club.club.application.usecase.ChangeParticipantRoleUseCase;
import com.mohaeng.club.club.domain.event.ParticipantClubRoleChangedEvent;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.*;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveGeneral;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveOfficer;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ChangeParticipantRole 은")
@ApplicationTest
class ChangeParticipantRoleTest {

    @Autowired
    private ChangeParticipantRoleUseCase changeParticipantRoleUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ApplicationEvents events;

    private Club club;
    private Member presidentMember;
    private Participant president;
    private Participant officer;
    private Participant general;

    @BeforeEach
    void init() {
        presidentMember = saveMember(memberRepository, member(null));
        club = saveClub(clubRepository, clubWithMember(presidentMember));
        president = club.participants().findByMemberId(presidentMember.id()).get();
        officer = saveOfficer(memberRepository, club);
        general = saveGeneral(memberRepository, club);
        flushAndClear();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
        club = clubRepository.findById(club.id()).get();
    }

    @Test
    void 대상의_역할을_변경한다() {
        // given
        ClubRole changed = club.findDefaultRoleByCategory(GENERAL);

        // when
        changeParticipantRoleUseCase.command(
                new ChangeParticipantRoleUseCase.Command(
                        presidentMember.id(),
                        club.id(),
                        officer.id(),
                        changed.id()
                )
        );
        flushAndClear();

        // then
        assertThat(club.findParticipantById(officer.id()).clubRole().id())
                .isEqualTo(changed.id());
    }

    @Test
    void 역할_변경_이후_알림을_위해_이벤트를_발행한다() {
        // when
        대상의_역할을_변경한다();

        // then
        assertThat(events.stream(ParticipantClubRoleChangedEvent.class).count()).isEqualTo(1L);
    }

    @Test
    void 회장이_아닌_경우_예외가_발생한다() {
        // given
        ClubRole changed = club.findDefaultRoleByCategory(OFFICER);

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                changeParticipantRoleUseCase.command(
                        new ChangeParticipantRoleUseCase.Command(
                                officer.member().id(),
                                club.id(),
                                general.id(),
                                changed.id()
                        ))
        ).exceptionType();
        flushAndClear();

        // then
        assertAll(
                () -> assertThat(events.stream(ParticipantClubRoleChangedEvent.class).count()).isEqualTo(0),
                () -> assertThat(club.findParticipantById(general.id()).clubRole()).isEqualTo(club.findDefaultRoleByCategory(GENERAL)),
                () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE)
        );
    }

    @Test
    void 회장으로_변경하려는_경우_예외가_발생한다() {
        // given
        ClubRole changed = club.findDefaultRoleByCategory(PRESIDENT);

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                changeParticipantRoleUseCase.command(
                        new ChangeParticipantRoleUseCase.Command(
                                president.member().id(),
                                club.id(),
                                general.id(),
                                changed.id()
                        ))
        ).exceptionType();
        flushAndClear();

        // then
        assertAll(
                () -> assertThat(events.stream(ParticipantClubRoleChangedEvent.class).count()).isEqualTo(0),
                () -> assertThat(club.findParticipantById(general.id()).clubRole()).isEqualTo(club.findDefaultRoleByCategory(GENERAL)),
                () -> assertThat(baseExceptionType).isEqualTo(NOT_CHANGE_PRESIDENT_ROLE)
        );
    }

    @Test
    void 존재하지_않는_참가자의_경우_예외가_발생한다() {
        // given
        ClubRole changed = club.findDefaultRoleByCategory(OFFICER);

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                changeParticipantRoleUseCase.command(
                        new ChangeParticipantRoleUseCase.Command(
                                president.member().id(),
                                club.id(),
                                general.id() + 100L,
                                changed.id()
                        ))
        ).exceptionType();
        flushAndClear();

        // then
        assertAll(
                () -> assertThat(events.stream(ParticipantClubRoleChangedEvent.class).count()).isEqualTo(0),
                () -> assertThat(club.findParticipantById(general.id()).clubRole()).isEqualTo(club.findDefaultRoleByCategory(GENERAL)),
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT)
        );
    }

    @Test
    void 역할이_없거나_다른_모임의_역할인_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeParticipantRoleUseCase.command(
                        new ChangeParticipantRoleUseCase.Command(
                                president.member().id(),
                                club.id(),
                                general.id(),
                                1000L
                        ))
        ).exceptionType();
        flushAndClear();

        // then
        assertAll(
                () -> assertThat(events.stream(ParticipantClubRoleChangedEvent.class).count()).isEqualTo(0),
                () -> assertThat(club.findParticipantById(general.id()).clubRole()).isEqualTo(club.findDefaultRoleByCategory(GENERAL)),
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE)
        );
    }
}