package com.mohaeng.common.fixtures;

import com.mohaeng.club.club.domain.model.Club;
import org.springframework.test.util.ReflectionTestUtils;

import static com.mohaeng.common.fixtures.MemberFixture.MALLANG_WITH_ID;

public class ClubFixture {

    public static final String ANA_NAME = "AMA";
    public static final String ANA_DESCRIPTION = "알고리즘 동아리";
    public static final int ANA_MAX_PARTICIPANT_COUNT = 100;
    public static final Club ANA_CLUB = new Club(ANA_NAME, ANA_DESCRIPTION, ANA_MAX_PARTICIPANT_COUNT, MALLANG_WITH_ID);

    public static final int FULL_PARTICIPANT_COUNT = 1;
    public static final Club FULL_CLUB = new Club(ANA_NAME, ANA_DESCRIPTION, FULL_PARTICIPANT_COUNT, MALLANG_WITH_ID);

    public static Club club(final Long id) {
        Club club = new Club(ANA_NAME, ANA_DESCRIPTION, ANA_MAX_PARTICIPANT_COUNT, MALLANG_WITH_ID);
        ReflectionTestUtils.setField(club, "id", id);
        return club;
    }
}
