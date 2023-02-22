package com.mohaeng.club.club.application.service;

import com.mohaeng.club.club.application.usecase.LeaveClubUseCase;
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

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB;
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
@DisplayName("LeaveClub 은")
@ApplicationTest
class LeaveClubTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private LeaveClubUseCase leaveClubUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

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
    }

    @Test
    void 회원을_모임에서_탈퇴시킨다() {
        // given
        assertThat(club.existParticipantByMemberId(general.member().id())).isTrue();
        int before = club.currentParticipantCount();

        // when
        leaveClubUseCase.command(
                new LeaveClubUseCase.Command(general.member().id(), club.id())
        );
        leaveClubUseCase.command(
                new LeaveClubUseCase.Command(officer.member().id(), club.id())
        );

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isFalse(),
                () -> assertThat(club.existParticipantByMemberId(officer.member().id())).isFalse(),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(before - 2)
        );
    }

    @Test
    void 모임이_존재하지_않는_경우_예외가_발생한다() {
        // given
        int currentParticipantCount = club.currentParticipantCount();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                leaveClubUseCase.command(
                        new LeaveClubUseCase.Command(officer.member().id(), 10000L)
                )).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
        );
    }

    @Test
    void 회원이_모임에_존재하지_않는_경우_예외를_발생시킨다() {
        // given
        int currentParticipantCount = club.currentParticipantCount();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                leaveClubUseCase.command(
                        new LeaveClubUseCase.Command(10000L, club.id())
                )).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
        );
    }

    @Test
    void 회장은_모임에서_탈퇴할_수_없다() {
        // given
        int currentParticipantCount = club.currentParticipantCount();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                leaveClubUseCase.command(
                        new LeaveClubUseCase.Command(presidentMember.id(), club.id())
                )).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(PRESIDENT_CAN_NOT_LEAVE_CLUB),
                () -> assertThat(club.existParticipantByMemberId(presidentMember.id())).isTrue(),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
        );
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}