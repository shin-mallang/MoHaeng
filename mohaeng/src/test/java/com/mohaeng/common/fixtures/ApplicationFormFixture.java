package com.mohaeng.common.fixtures;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;

public class ApplicationFormFixture {

    public static ApplicationForm applicationForm(final Club club, final Member member) {
        return ApplicationForm.create(club, member);
    }

    public static ApplicationForm applicationForm() {
        return ApplicationForm.create(club(1L), member(1L));
    }
}
