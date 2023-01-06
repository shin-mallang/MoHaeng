package com.mohaeng.clubrole.domain.repository;

import com.mohaeng.clubrole.domain.model.ClubRole;

import java.util.List;
import java.util.Optional;

public interface ClubRoleRepository {

    List<ClubRole> saveAll(final List<ClubRole> defaultClubRoles);

    Optional<ClubRole> findById(final Long id);
}
