package com.mohaeng.clubrole.domain.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;

import java.util.List;
import java.util.Optional;

public interface ClubRoleRepository {

    ClubRole save(final ClubRole clubRole);

    List<ClubRole> saveAll(final List<ClubRole> defaultClubRoles);

    Optional<ClubRole> findById(final Long id);

    Optional<ClubRole> findDefaultGeneralRoleByClub(final Club club);

    Optional<ClubRole> findWithClubById(final Long clubRoleId);

    List<ClubRole> findTop2ByClubAndClubRoleCategory(final Club club, final ClubRoleCategory clubRoleCategory);

    ClubRole findDefaultRoleByClubAndClubRoleCategory(final Club club, final ClubRoleCategory clubRoleCategory);

    void deleteAllByClubId(final Long clubId);

    void delete(final ClubRole clubRole);
}
