package com.mohaeng.club.applicationform.application;

import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.club.application.service.ClubCommandTest;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.common.fixtures.ClubFixture.fullClubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;

public abstract class ApplicationFormCommandTest extends ClubCommandTest {
    @Autowired
    protected ApplicationFormRepository applicationFormRepository;

    protected Member applicant;
    protected Club fullClub;

    @BeforeEach
    protected void 가입_신청자와_가득_찬_모임을_생성한다() {
        applicant = saveMember(memberRepository, member(null));
        fullClub = saveClub(clubRepository, fullClubWithMember(presidentMember));
    }
}
