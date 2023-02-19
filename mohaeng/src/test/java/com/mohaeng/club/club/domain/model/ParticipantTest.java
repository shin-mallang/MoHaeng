package com.mohaeng.club.club.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Participant 은")
class ParticipantTest {

    private final Club club = club(1L);
    private final Map<ClubRoleCategory, ClubRole> clubRoleMap =
            ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));

    @Test
    void isManager_는_회장_혹은_임원인_경우_true를_반환한다() {
        // given
        Participant president = new Participant(member(1L), club, clubRoleMap.get(PRESIDENT));
        Participant officer = new Participant(member(2L), club, clubRoleMap.get(OFFICER));
        Participant general = new Participant(member(3L), club, clubRoleMap.get(GENERAL));

        // when & then
        assertThat(president.isManager()).isTrue();
        assertThat(officer.isManager()).isTrue();
        assertThat(general.isManager()).isFalse();
        System.out.println(club.participants().participants().size());
    }

    @Test
    void isPresident_는_회장인_경우_true를_반환한다() {
        // given
        Participant president = new Participant(member(1L), club, clubRoleMap.get(PRESIDENT));
        Participant officer = new Participant(member(2L), club, clubRoleMap.get(OFFICER));
        Participant general = new Participant(member(3L), club, clubRoleMap.get(GENERAL));

        // when & then
        assertThat(president.isPresident()).isTrue();
        assertThat(officer.isPresident()).isFalse();
        assertThat(general.isPresident()).isFalse();
    }

    @Test
    void expel_시_대상_참여자를_모임에서_추방한다() {
        // given

        // when

        // then
    }

    @Test
    void expel_시_회장이_아닌_경우_추방할_수_없다() {
        // given

        // when

        // then
    }

    @Test
    void expel_시_대상_참여자와_같은_모임이_아닌_경우_추방할_수_없다() {
        // given

        // when

        // then
    }
}