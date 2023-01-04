package com.mohaeng.domain.club.repository.role;

import com.mohaeng.domain.club.model.role.ClubRole;

import java.util.List;

public interface ClubRoleRepository {

    List<ClubRole> saveAll(final List<ClubRole> defaultClubRoles);
}
