package com.mohaeng.common.fixtures;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

public class ClubRoleFixture {

    public static List<ClubRole> clubRolesWithId(final Club club) {
        List<ClubRole> clubRoles = ClubRole.defaultRoles(club);
        clubRoles.forEach(it -> ReflectionTestUtils.setField(it, "id", 1L));
        return clubRoles;
    }

    public static ClubRole presidentRole(final String name, final Club club) {
        return new ClubRole(name, ClubRoleCategory.PRESIDENT, club, true);
    }

    public static ClubRole generalRole(final String name, final Club club) {
        return new ClubRole(name, ClubRoleCategory.GENERAL, club, false);
    }

    public static ClubRole officerRole(final String name, final Club club) {
        return new ClubRole(name, ClubRoleCategory.OFFICER, club, false);
    }
}
