package com.mohaeng.club.club.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ClubRoles 은")
class ClubRolesTest {

    private final Club club = mock(Club.class);

    @Test
    void defaultRoles_는_역할의_카테고리별_기본_역할_1개씩을_각각_생성하여_반환한다() {
        // when
        ClubRoles clubRoles = ClubRoles.defaultRoles(club);

        // then
        assertThat(clubRoles.clubRoles().stream().map(ClubRole::clubRoleCategory).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(PRESIDENT, OFFICER, GENERAL));
        clubRoles.clubRoles().forEach(it -> assertThat(it.isDefault()).isTrue());
    }

    @Test
    void findDefaultRoleByCategory_는_주어진_카테고리에_해당하는_역할들_중_기본_역할을_반환한다() {
        // given
        ClubRoles clubRoles = ClubRoles.defaultRoles(club);

        // when & then
        assertThat(clubRoles.findDefaultRoleByCategory(PRESIDENT).clubRoleCategory()).isEqualTo(PRESIDENT);
        assertThat(clubRoles.findDefaultRoleByCategory(OFFICER).clubRoleCategory()).isEqualTo(OFFICER);
        assertThat(clubRoles.findDefaultRoleByCategory(GENERAL).clubRoleCategory()).isEqualTo(GENERAL);
    }
}