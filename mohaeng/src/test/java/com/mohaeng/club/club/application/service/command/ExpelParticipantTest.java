package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.usecase.command.ExpelParticipantUseCase;
import com.mohaeng.club.club.domain.event.ExpelParticipantEvent;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT;
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
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ExpelParticipant 은")
@ApplicationTest
class ExpelParticipantTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ExpelParticipant expelParticipant;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

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

    @Test
    void 회원을_모임에서_추방히시키고_이벤트를_발행한다() {
        // given
        int before = club.currentParticipantCount();

        // when
        expelParticipant.command(
                new ExpelParticipantUseCase.Command(presidentMember.id(), club.id(), officer.id())
        );

        // then
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(club.existParticipantByMemberId(officer.member().id())).isFalse(),
                () -> assertThat(club.currentParticipantCount()).isEqualTo(before - 1),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(1L)
        );
    }

    @Test
    void 추방하려는_회원이_모임에_존재하지_않는_경우_예외를_발생시킨다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(presidentMember.id(), club.id(), 1000L)
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }

    @Test
    void 회장이_아닌_경우_추방할_수_없다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(officer.member().id(), club.id(), general.id())
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT),
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isTrue(),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }

    @Test
    void 모임이_없는경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(president.member().id(), 1000L, general.id())
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB),
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isTrue(),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }

    @Test
    void 요청자가_해당_모임에_가입하지_않은_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(president.member().id() + 123L, club.id(), general.id())
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isTrue(),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}