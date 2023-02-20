package com.mohaeng.club.club.domain.model;

import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
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

    private Participant president;
    private final Participant officer = new Participant(member(2L), club, clubRoleMap.get(OFFICER));
    private final Participant general = new Participant(member(3L), club, clubRoleMap.get(GENERAL));

    @BeforeEach
    void init() {
        president = club.findPresident();
        club.participants().participants().add(officer);
        club.participants().participants().add(general);
    }

    @Test
    void isManager_는_회장_혹은_임원인_경우_true를_반환한다() {
        // when & then
        assertThat(president.isManager()).isTrue();
        assertThat(officer.isManager()).isTrue();
        assertThat(general.isManager()).isFalse();
    }

    @Test
    void isPresident_는_회장인_경우_true를_반환한다() {
        // when & then
        assertThat(president.isPresident()).isTrue();
        assertThat(officer.isPresident()).isFalse();
        assertThat(general.isPresident()).isFalse();
    }
}