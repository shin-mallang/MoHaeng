package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.ApplicationFormCommandTest;
import com.mohaeng.club.applicationform.application.usecase.ApproveApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.club.applicationform.domain.event.OfficerApproveApplicationEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION;
import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.common.util.RepositoryUtil.saveApplicationForm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ApproveApplicationForm(가입 신청서 수락) 은")
@ApplicationTest
class ApproveApplicationFormTest extends ApplicationFormCommandTest {

    @Autowired
    private ApproveApplicationFormUseCase approveApplicationFormUseCase;

    @Test
    void 회원을_모임에_기본_역할로_가입시킨다() {
        // given
        ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));
        int before = club.currentParticipantCount();

        // when
        approveApplicationFormUseCase.command(
                new ApproveApplicationFormUseCase.Command(applicationForm.id(), presidentMember.id())
        );

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertThat(club.existParticipantByMemberId(applicant.id())).isTrue();
        assertThat(applicationFormRepository.findById(applicationForm.id()).orElse(null).processed()).isTrue();
        assertThat(club.currentParticipantCount()).isEqualTo(before + 1);
    }

    @Test
    void 회장이_가입_신청을처리한_경우_이벤트는_한개만_발행한다() {
        // when
        회원을_모임에_기본_역할로_가입시킨다();

        // then
        assertAll(
                () -> assertThat(events.stream(BaseEvent.class).count()).isEqualTo(1L),
                () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                () -> assertThat(events.stream(OfficerApproveApplicationEvent.class).count()).isEqualTo(0L)
        );
    }

    @Test
    @DisplayName("임원진이 가입 신청을 처리한 경우 이벤트는 두개가 발행한다.")
    void success_test_3() {
        // given
        ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));

        // when
        approveApplicationFormUseCase.command(
                new ApproveApplicationFormUseCase.Command(applicationForm.id(), officer.member().id())
        );

        // then
        assertAll(
                () -> assertThat(events.stream(BaseEvent.class).count()).isEqualTo(2L),
                () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                () -> assertThat(events.stream(OfficerApproveApplicationEvent.class).count()).isEqualTo(1L)
        );
    }

    @Test
    @DisplayName("관리자가 아닌 경우 회원을 모임에 가입시킬 수 없다.")
    void fail_test_1() {
        // given
        ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));

        // when
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                approveApplicationFormUseCase.command(
                        new ApproveApplicationFormUseCase.Command(applicationForm.id(), general.member().id())
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
    void fail_test_2() {
        // given
        ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(club, applicant));
        approveApplicationFormUseCase.command(
                new ApproveApplicationFormUseCase.Command(applicationForm.id(), officer.member().id())
        );

        // when
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                approveApplicationFormUseCase.command(
                        new ApproveApplicationFormUseCase.Command(applicationForm.id(), presidentMember.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(ALREADY_PROCESSED);
    }

    @Test
    @DisplayName("모임이 가득 찬 경우 더이상 회원을 받을 수 없다.")
    void fail_test_3() {
        // given
        ApplicationForm applicationForm = saveApplicationForm(applicationFormRepository, ApplicationForm.create(fullClub, applicant));

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                approveApplicationFormUseCase.command(
                        new ApproveApplicationFormUseCase.Command(applicationForm.id(), presidentMember.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(CLUB_IS_FULL);
    }
}