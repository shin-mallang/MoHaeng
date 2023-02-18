package com.mohaeng.club.participant.domain.model;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_CLUB;
import static com.mohaeng.common.fixtures.MemberFixture.MALLANG;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Participant 은")
class ParticipantTest {

    private final Club club = ANA_CLUB;
    private final Map<ClubRoleCategory, ClubRole> clubRoleMap =
            ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));

    @Test
    void isManager_는_회장_혹은_임원인_경우_true를_반환한다() {
        // given
        Participant president = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(PRESIDENT));
        Participant officer = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(OFFICER));
        Participant general = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(GENERAL));

        // when & then
        assertThat(president.isManager()).isTrue();
        assertThat(officer.isManager()).isTrue();
        assertThat(general.isManager()).isFalse();
    }

    @Test
    void isPresident_는_회장인_경우_true를_반환한다() {
        // given
        Participant president = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(PRESIDENT));
        Participant officer = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(OFFICER));
        Participant general = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(GENERAL));

        // when & then
        assertThat(president.isPresident()).isTrue();
        assertThat(officer.isPresident()).isFalse();
        assertThat(general.isPresident()).isFalse();
    }
}