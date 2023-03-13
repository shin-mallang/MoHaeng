package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.ApplicationFormCommandTest;
import com.mohaeng.club.applicationform.application.usecase.FillOutApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.FillOutApplicationFormEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_REQUEST_JOIN_CLUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ApplicationTest
@DisplayName("FillOutApplicationForm(가입 신청서 작성) 은")
class FillOutApplicationFormTest extends ApplicationFormCommandTest {

    @Autowired
    private FillOutApplicationFormUseCase fillOutApplicationFormUseCase;

    @Test
    void 가입_신청을_할_수_있다() {
        // when
        Long applicationFormId = fillOutApplicationFormUseCase.command(
                new FillOutApplicationFormUseCase.Command(applicant.id(), club.id())
        );

        // then
        assertThat(applicationFormRepository.findById(applicationFormId)).isPresent();
    }

    @Test
    void 가입이_거절되었더라도_다시_신청할_수_있다() {
        // given
        president = club.findParticipantByMemberId(presidentMember.id());  // equals 비교를 위해 영속성 컨텍스트에 올리기
        ApplicationForm saved = applicationFormRepository.save(ApplicationForm.create(club, applicant));
        saved.reject(president);

        // when
        Long applicationFormId = fillOutApplicationFormUseCase.command(
                new FillOutApplicationFormUseCase.Command(applicant.id(), club.id())
        );

        // then
        assertThat(applicationFormRepository.findById(applicationFormId)).isPresent();
    }

    @Test
    void 모임이_가득_찬_경우에도_가입_신청을_보낼_수_있다() {
        // when
        Long applicationFormId = fillOutApplicationFormUseCase.command(
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
                fillOutApplicationFormUseCase.command(
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
                fillOutApplicationFormUseCase.command(
                        new FillOutApplicationFormUseCase.Command(applicant.id(), club.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(ALREADY_MEMBER_JOINED_CLUB);
    }
}
