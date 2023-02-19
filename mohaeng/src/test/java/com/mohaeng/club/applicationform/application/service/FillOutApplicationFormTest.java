package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.usecase.FillOutApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.FillOutApplicationFormEvent;
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

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_REQUEST_JOIN_CLUB;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.ClubFixture.fullClubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ApplicationTest
@DisplayName("FillOutApplicationForm 은")
class FillOutApplicationFormTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private FillOutApplicationForm fillOutApplicationForm;

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

    @BeforeEach
    void init() {
        presidentMember = saveMember(memberRepository, member(null));
        applicant = saveMember(memberRepository, member(null));
        club = saveClub(clubRepository, clubWithMember(presidentMember));
        fullClub = saveClub(clubRepository, fullClubWithMember(presidentMember));
        president = club.participants().findByMemberId(presidentMember.id()).get();
    }

    @Test
    void 가입_신청을_할_수_있다() {
        // when
        Long applicationFormId = fillOutApplicationForm.command(
                new FillOutApplicationFormUseCase.Command(applicant.id(), club.id())
        );

        // then
        assertThat(applicationFormRepository.findById(applicationFormId)).isPresent();
    }

    @Test
    void 가입이_거절되었더라도_다시_신청할_수_있다() {
        // given
        ApplicationForm saved = applicationFormRepository.save(ApplicationForm.create(club, applicant));
        saved.reject(president);

        // when
        Long applicationFormId = fillOutApplicationForm.command(
                new FillOutApplicationFormUseCase.Command(applicant.id(), club.id())
        );

        // then
        assertThat(applicationFormRepository.findById(applicationFormId)).isPresent();
    }

    @Test
    void 모임이_가득_찬_경우에도_가입_신청을_보낼_수_있다() {
        // when
        Long applicationFormId = fillOutApplicationForm.command(
                new FillOutApplicationFormUseCase.Command(applicant.id(), fullClub.id())
        );

        // then
        assertThat(applicationFormRepository.findById(applicationFormId)).isPresent();
    }

    @Test
    void 가입_신청을_하게되면_가입_신청_이벤트가_발행된다() {
        // when
        가입_신청을_할_수_있다();

        // then
        assertThat(events.stream(FillOutApplicationFormEvent.class).count()).isEqualTo(1L);
    }

    @Test
    void 이미_처리되지_않은_가입_신청서가_있는_경우_신청할_수_없다() {
        // given
        가입_신청을_할_수_있다();

        // when & then
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                fillOutApplicationForm.command(
                        new FillOutApplicationFormUseCase.Command(applicant.id(), club.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(ALREADY_REQUEST_JOIN_CLUB);
    }

    @Test
    void 이미_가입된_사람이_또다시_신청하는_경우_예외가_발생한다() {
        // given
        club.registerParticipant(applicant);

        // when & then
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                fillOutApplicationForm.command(
                        new FillOutApplicationFormUseCase.Command(applicant.id(), club.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(ALREADY_MEMBER_JOINED_CLUB);
    }
}