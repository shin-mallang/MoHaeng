package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.usecase.RejectApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.club.applicationform.domain.event.OfficerRejectApplicationEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.ClubFixture.fullClubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveGeneral;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveOfficer;
import static com.mohaeng.common.util.RepositoryUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ApplicationTest
@DisplayName("RejectApplicationForm(가입 신청서 거절) 은")
class RejectApplicationFormTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RejectApplicationFormUseCase rejectApplicationFormUseCase;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ApplicationEvents events;

    private Member applicant;
    private Club club;
    private Club fullClub;
    private Member presidentMember;
    private Participant president;
    private Participant officer;
    private Participant general;

    @BeforeEach
    void init() {
        presidentMember = saveMember(memberRepository, member(null));
        applicant = saveMember(memberRepository, member(null));
        club = saveClub(clubRepository, clubWithMember(presidentMember));
        fullClub = saveClub(clubRepository, fullClubWithMember(presidentMember));
        president = club.participants().findByMemberId(presidentMember.id()).get();
        officer = saveOfficer(memberRepository, club);
        general = saveGeneral(memberRepository, club);
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        void 가입_신청서_거절_시_처리상태로_만든_후_회원을_모임에_가입시키지_않는다() {
            // given
            ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));

            // when
            rejectApplicationFormUseCase.command(
                    new RejectApplicationFormUseCase.Command(applicationForm.id(), presidentMember.id())
            );

            // then
            flushAndClear();
            club = clubRepository.findById(club.id()).orElse(null);
            ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
            assertAll(
                    () -> assertThat(applicationForm.club().existParticipantByMemberId(applicant.id())).isFalse(),
                    () -> assertThat(findApplicationForm.processed()).isTrue()
            );
        }

        @Test
        void 회장이_가입_신청을_거절한_경우_이벤트는_한개만_발행된다() {
            // when
            가입_신청서_거절_시_처리상태로_만든_후_회원을_모임에_가입시키지_않는다();

            // then
            flushAndClear();
            club = clubRepository.findById(club.id()).orElse(null);
            assertAll(
                    () -> assertThat(club.existParticipantByMemberId(applicant.id())).isFalse(),
                    () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(OfficerRejectApplicationEvent.class).count()).isEqualTo(0L)
            );
        }

        @Test
        void 임원진이_가입_신청을_처리한_경우_이벤트는_두개가_발행한다() {
            // given
            ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));

            // when
            rejectApplicationFormUseCase.command(
                    new RejectApplicationFormUseCase.Command(applicationForm.id(), officer.member().id())
            );

            // then

            ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
            assertAll(
                    () -> assertThat(applicationForm.club().existParticipantByMemberId(applicant.id())).isFalse(),
                    () -> assertThat(findApplicationForm.processed()).isTrue(),
                    () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(OfficerRejectApplicationEvent.class).count()).isEqualTo(1L)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        void 관리자가_아닌_경우_회원의_가입_신청을_거절할_수_없다() {
            // given
            ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));

            // when
            BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                    rejectApplicationFormUseCase.command(
                            new RejectApplicationFormUseCase.Command(applicationForm.id(), general.member().id())
                    )
            ).exceptionType();

            // then
            ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id())
                    .orElseThrow(IllegalArgumentException::new);
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION),
                    () -> assertThat(applicationForm.club().existParticipantByMemberId(applicant.id())).isFalse(),
                    () -> assertThat(findApplicationForm.processed()).isFalse()
            );
        }

        @Test
        @DisplayName("이미 처리된 신청서의 경우 또다시 처리될 수 없다.")
        void 이미_처리된_신청서의_경우_또다시_처리될_수_없다() {
            // given
            ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));
            rejectApplicationFormUseCase.command(
                    new RejectApplicationFormUseCase.Command(applicationForm.id(), presidentMember.id())
            );

            // when
            BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                    rejectApplicationFormUseCase.command(
                            new RejectApplicationFormUseCase.Command(applicationForm.id(), presidentMember.id())
                    )
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(ALREADY_PROCESSED);
        }
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}