package com.mohaeng.clubrole.infrastructure.persistence.database.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaClubRoleRepository extends JpaRepository<ClubRole, Long>, ClubRoleRepository {

    @Override
    default List<ClubRole> saveAll(final List<ClubRole> entities) {
        return saveAll((Iterable<ClubRole>) entities);
    }

    @Override
    @Query("select cr from ClubRole cr where cr.clubRoleCategory = 'GENERAL' and cr.isDefault = true")
    Optional<ClubRole> findDefaultGeneralRoleByClub(final Club club);

    @Override
    @Query("select cr from ClubRole cr join fetch cr.club where cr.id = :clubRoleId")
    Optional<ClubRole> findWithClubById(@Param("clubRoleId") final Long clubRoleId);

    @Override
    @Query("select cr from ClubRole cr where cr.club = :club and cr.clubRoleCategory = :clubRoleCategory and cr.isDefault = true")
    ClubRole findDefaultRoleByClubAndClubRoleCategory(@Param("club") final Club club,
                                                      @Param("clubRoleCategory") final ClubRoleCategory clubRoleCategory);

    @Override
    @Modifying
    @Query("delete from ClubRole cr where cr.club.id = :clubId")
    void deleteAllByClubId(@Param("clubId") final Long clubId);
}
