package com.mohaeng.clubrole.domain.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;

import java.util.List;
import java.util.Optional;

public interface ClubRoleRepository {

    ClubRole save(final ClubRole clubRole);

    List<ClubRole> saveAll(final List<ClubRole> defaultClubRoles);

    Optional<ClubRole> findById(final Long id);

    Optional<ClubRole> findDefaultGeneralRoleByClub(final Club club);

    void deleteAllByClubId(final Long clubId);
}
