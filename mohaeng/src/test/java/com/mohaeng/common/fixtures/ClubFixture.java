package com.mohaeng.common.fixtures;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import org.springframework.test.util.ReflectionTestUtils;

import static com.mohaeng.common.fixtures.MemberFixture.member;

public class ClubFixture {

    public static final String ANA_NAME = "AMA";
    public static final String ANA_DESCRIPTION = "알고리즘 동아리";
    public static final int ANA_MAX_PARTICIPANT_COUNT = 100;
    public static final int FULL_PARTICIPANT_COUNT = 1;

    public static Club club(final Long id, final String name, final String des, final int maxParticipantCount, final int currentParticipantCount) {
        Club club = new Club(name, des, maxParticipantCount, member(1L));
        ReflectionTestUtils.setField(club, "id", id);
        ReflectionTestUtils.setField(club, "currentParticipantCount", currentParticipantCount);
        return club;
    }

    public static Club club(final Long id) {
        Club club = new Club(ANA_NAME, ANA_DESCRIPTION, ANA_MAX_PARTICIPANT_COUNT, member(1L));
        ReflectionTestUtils.setField(club, "id", id);
        return club;
    }

    public static Club clubWithMember(final Member member) {
        return new Club(ANA_NAME, ANA_DESCRIPTION, ANA_MAX_PARTICIPANT_COUNT, member);
    }

    public static Club fullClubWithMember(final Member member) {
        return new Club(ANA_NAME, ANA_DESCRIPTION, FULL_PARTICIPANT_COUNT, member);
    }
}
