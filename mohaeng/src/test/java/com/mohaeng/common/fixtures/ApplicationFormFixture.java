package com.mohaeng.common.fixtures;

import com.mohaeng.applicationform.application.usecase.WriteApplicationFormUseCase;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import org.springframework.test.util.ReflectionTestUtils;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;

public class ApplicationFormFixture {

    public static WriteApplicationFormUseCase.Command requestJoinClubUseCaseCommand(final Long applicantId, final Long targetClubId) {
        return new WriteApplicationFormUseCase.Command(applicantId, targetClubId);
    }

    public static ApplicationForm applicationForm(final Long memberId, final Long clubId, final Long applicationFormId) {
        ApplicationForm applicationForm = ApplicationForm.create(member(memberId), club(clubId));
        ReflectionTestUtils.setField(applicationForm, "id", applicationFormId);
        return applicationForm;
    }
}
