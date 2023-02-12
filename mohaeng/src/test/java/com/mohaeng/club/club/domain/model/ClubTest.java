package com.mohaeng.club.club.domain.model;

import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.MemberFixture.MALLANG;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Club 은")
class ClubTest {

    private static final String NAME = "ANA";
    private static final String DESCRIPTION = "알고리즘 동아리";
    private static final int MAX_PARTICIPANT_COUNT = 10;
    private final Member member = MALLANG;

    @Test
    void 생성_시_모임의_기본_역할과_회장을_같이_저장한다() {
        // when
        Club club = new Club(NAME, DESCRIPTION, MAX_PARTICIPANT_COUNT, member);

        // then
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(3);
        assertThat(club.clubRoles().clubRoles().stream().map(ClubRole::clubRoleCategory).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(GENERAL, OFFICER, PRESIDENT));
        assertThat(club.participants().participants().size()).isEqualTo(1);
        assertThat(club.participants().participants().get(0).clubRole().clubRoleCategory()).isEqualTo(PRESIDENT);
    }

    @Test
    void 생성_시_모임의_회원_수는_1이다() {
        // when
        Club club = new Club(NAME, DESCRIPTION, MAX_PARTICIPANT_COUNT, member);

        // then
        assertThat(club.currentParticipantCount()).isEqualTo(1);
    }
}