package com.mohaeng.club.club.application.service;

import com.mohaeng.club.club.application.usecase.DelegatePresidentUseCase;
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
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_DELEGATE_PRESIDENT;
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
@DisplayName("DelegatePresident 은")
@ApplicationTest
class DelegatePresidentTest {

    @Autowired
    private DelegatePresidentUseCase delegatePresidentUseCase;

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
    void 회장_역할을_위임한다() {
        // when
        delegatePresidentUseCase.command(
                new DelegatePresidentUseCase.Command(
                        presidentMember.id(), club.id(), general.id()
                )
        );
        flushAndClear();

        // then
        general = club.findParticipantById(general.id());
        assertThat(general.isPresident()).isTrue();
    }

    @Test
    void 기존_회장은_일반_회원이_된다() {
        // when
        회장_역할을_위임한다();

        // then
        president = club.findParticipantById(president.id());
        assertThat(president.isManager()).isFalse();
    }

    @Test
    void 모임이_없다면_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                delegatePresidentUseCase.command(
                        new DelegatePresidentUseCase.Command(
                                presidentMember.id(), 123124L, general.id()
                        )
                )).exceptionType();

        // then
        general = club.findParticipantById(general.id());
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB),
                () -> assertThat(general.isPresident()).isFalse()
        );
    }

    @Test
    void 요청자가_해당_모임에_참여하지_않은_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                delegatePresidentUseCase.command(
                        new DelegatePresidentUseCase.Command(
                                123124123L, club.id(), general.id()
                        )
                )).exceptionType();

        // then
        general = club.findParticipantById(general.id());
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                () -> assertThat(general.isPresident()).isFalse()
        );
    }

    @Test
    void 요청자가_회장이_아닌_경우_예외가_발생한다() {
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                delegatePresidentUseCase.command(
                        new DelegatePresidentUseCase.Command(
                                officer.member().id(), club.id(), general.id()
                        )
                )).exceptionType();

        // then
        general = club.findParticipantById(general.id());
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELEGATE_PRESIDENT),
                () -> assertThat(general.isPresident()).isFalse()
        );
    }

    @Test
    void 대상자가_해당_모임에_없다면_예외가_발생한다() {
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                delegatePresidentUseCase.command(
                        new DelegatePresidentUseCase.Command(
                                presidentMember.id(), club.id(), 11111L
                        )
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
    }
}