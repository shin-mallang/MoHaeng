package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.applicationform.domain.event.DeleteApplicationFormEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.club.application.usecase.command.DeleteClubUseCase;
import com.mohaeng.club.club.domain.event.DeleteClubEvent;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubExceptionType.NO_AUTHORITY_DELETE_CLUB;
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
@DisplayName("DeleteClub 은")
@ApplicationTest
class DeleteClubTest {

    @Autowired
    private DeleteClubUseCase deleteClubUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

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
        applicationFormRepository.save(ApplicationForm.create(club, saveMember(memberRepository, member(null))));
        applicationFormRepository.save(ApplicationForm.create(club, saveMember(memberRepository, member(null))));
        applicationFormRepository.save(ApplicationForm.create(club, saveMember(memberRepository, member(null))));
        flushAndClear();
    }

    @Test
    @DisplayName("요청자가 모임의 회장인 경우, 모임의 가입자, 모임의 역할, 모임, 가입 신청서를 모두 제거한다.")
    void 요청자가_모임의_회장인_경우_모임의_가입자_모임의_역할_모임_가입_신청서를_모두_제거한다() {

        // when
        deleteClubUseCase.command(
                new DeleteClubUseCase.Command(presidentMember.id(), club.id())
        );

        assertAll(
                () -> assertThat(clubRepository.findById(club.id())).isEmpty(),
                () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class)
                        .getResultList().size())
                        .isEqualTo(0),
                () -> assertThat(em.createQuery("select p from Participant p", Participant.class)
                        .getResultList().size())
                        .isEqualTo(0),
                () -> assertThat(em.createQuery("select af from ApplicationForm af", ApplicationForm.class)
                        .getResultList().size())
                        .isEqualTo(0)
        );
    }

    @Test
    void 모임_제거_시_모임_제거_이벤트가_발행된다() {
        // when
        요청자가_모임의_회장인_경우_모임의_가입자_모임의_역할_모임_가입_신청서를_모두_제거한다();

        // then
        assertThat(events.stream(DeleteClubEvent.class).count()).isEqualTo(1L);
        assertThat(events.stream(DeleteApplicationFormEvent.class).count()).isEqualTo(1L);
    }

    @Test
    void 요청자가_모임의_회장이_아닌_경우_예외가_발생한다() {
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () -> deleteClubUseCase.command(
                new DeleteClubUseCase.Command(officer.member().id(), club.id())
        )).exceptionType();

        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELETE_CLUB),
                () -> assertThat(clubRepository.findById(club.id())).isNotEmpty()
        );
    }

    @Test
    void 모임이_없는_경우_예외가_발생한다() {
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () -> deleteClubUseCase.command(
                new DeleteClubUseCase.Command(officer.member().id(), 100L)
        )).exceptionType();

        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB),
                () -> assertThat(clubRepository.findById(club.id())).isNotEmpty()
        );
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}