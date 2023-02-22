package com.mohaeng.club.applicationform.domain;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.common.fixtures.ParticipantFixture;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.*;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION;
import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.ALREADY_EXIST_PARTICIPANT;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubFixture.fullClubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ApplicationForm 은")
class ApplicationFormTest {

    private final Member applicant = member(10000L);
    private final Club club = club(1L);
    private Participant general;
    private Participant president;
    private ApplicationForm applicationForm = applicationForm(club, applicant);
    private ApplicationForm applicationForm2 = applicationForm(club, applicant);

    @BeforeEach
    void init() {
        this.general = ParticipantFixture.mockGeneral(club);
        this.president = ParticipantFixture.mockPresident(club);
    }

    @Test
    void 수락될_수_있다() {
        // when
        applicationForm.approve(president);

        // then
        assertThat(applicationForm.processed()).isTrue();
    }

    @Test
    void 거절될_수_있다() {
        // when
        applicationForm.reject(president);

        // then
        assertThat(applicationForm.processed()).isTrue();
    }

    @Test
    void 이미_처리된_신청서가_또다시_처리되려는_경우_예외가_발생한다() {
        // given
        applicationForm.reject(president);

        // when
        BaseExceptionType baseExceptionType =
                assertThrows(ApplicationFormException.class, () ->
                        applicationForm.reject(president))
                        .exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(ALREADY_PROCESSED);
    }

    @Test
    void 수락_시_모임이_가득_찬_경우_예외가_발생한다() {
        // given
        Member member = member(10L);
        Club full = fullClubWithMember(member);
        ApplicationForm applicationForm = applicationForm(full, applicant);
        when(president.club()).thenReturn(full);

        // when
        BaseExceptionType baseExceptionType =
                assertThrows(ClubException.class, () ->
                        applicationForm.approve(president))
                        .exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(CLUB_IS_FULL);
    }

    @Test
    @DisplayName("수락 혹은 거절시 처리자가 권한이 없는 경우(회장 / 임원이 아닌 경우) 가입 신청은 처리되지 않은 상태로 유지된다")
    void 수락_혹은_거절시_처리자가_권한이_없는_경우_가입_신청은_처리되지_않은_상태로_유지된다() {
        // given
        ApplicationForm applicationForm = ApplicationForm.create(club, applicant);

        // when
        BaseExceptionType baseExceptionType =
                assertThrows(ApplicationFormException.class, () ->
                        applicationForm.reject(general))
                        .exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION);
        assertThat(applicationForm.processed()).isFalse();
    }

    @Test
    void 이미_가입된_회원의_경우_수락되는_경우_예외가_발생한다() {
        // given
        applicationForm.approve(president);

        // when
        BaseExceptionType baseExceptionType =
                assertThrows(ParticipantException.class, () ->
                        applicationForm2.approve(president))
                        .exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(ALREADY_EXIST_PARTICIPANT);
    }
}