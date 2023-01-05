package com.mohaeng.clubrole.domain.repository;

import com.mohaeng.clubrole.domain.model.ClubRole;

import java.util.List;

public interface ClubRoleRepository {

    List<ClubRole> saveAll(final List<ClubRole> defaultClubRoles);
}
