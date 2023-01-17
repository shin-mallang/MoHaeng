package com.mohaeng.common.fixtures;

import com.mohaeng.applicationform.application.usecase.RequestJoinClubUseCase;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;

public class ApplicationFormFixture {

    public static RequestJoinClubUseCase.Command requestJoinClubUseCaseCommand(final Long applicantId, final Long targetClubId) {
        return new RequestJoinClubUseCase.Command(applicantId, targetClubId);
    }

    public static ApplicationForm applicationForm(final Long memberId, final Long clubId, final Long applicationFormId) {
        ApplicationForm applicationForm = ApplicationForm.create(member(memberId), club(clubId));
        ReflectionTestUtils.setField(applicationForm, "id", applicationFormId);
        return applicationForm;
    }
}
