package com.mohaeng.club.club.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.ClubFixture.*;
import static com.mohaeng.common.fixtures.MemberFixture.MALLANG;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Club 은")
class ClubTest {

    @Test
    void 생성_시_모임의_기본_역할과_회장을_같이_저장한다() {
        // when
        Club club = new Club(ANA_NAME, ANA_DESCRIPTION, ANA_MAX_PARTICIPANT_COUNT, MALLANG);

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
        Club club = new Club(ANA_NAME, ANA_DESCRIPTION, ANA_MAX_PARTICIPANT_COUNT, MALLANG);

        // then
        assertThat(club.currentParticipantCount()).isEqualTo(1);
    }
}