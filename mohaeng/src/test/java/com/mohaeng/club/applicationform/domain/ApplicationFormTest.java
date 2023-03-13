package com.mohaeng.club.applicationform.domain;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION;
import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.ALREADY_EXIST_PARTICIPANT;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.ClubFixture.fullClubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ApplicationForm(가입 신청서) 은")
class ApplicationFormTest {

    private Member presidentMember;
    private Member applicant;
    private Club club;
    private Participant general;
    private Participant president;
    private ApplicationForm applicationForm;
    private ApplicationForm applicationForm2;

    @BeforeEach
    void init() {
        presidentMember = member(1L);
        applicant = member(2L);
        club = clubWithMember(presidentMember);
        general = club.participants().register(member(3L), club, club.findDefaultRoleByCategory(ClubRoleCategory.GENERAL));
        president = club.findPresident();
        applicationForm = ApplicationForm.create(club, applicant);
        applicationForm2 = ApplicationForm.create(club, applicant);
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
        Club full = fullClubWithMember(presidentMember);
        president = full.findPresident();
        ApplicationForm applicationForm = applicationForm(full, applicant);

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
