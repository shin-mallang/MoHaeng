package com.mohaeng.clubrole.infrastructure.persistence.database.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaClubRoleRepository extends JpaRepository<ClubRole, Long>, ClubRoleRepository {

    @Override
    default List<ClubRole> saveAll(List<ClubRole> entities) {
        return saveAll((Iterable<ClubRole>) entities);
    }

    @Override
    @Query("select cr from ClubRole cr where cr.clubRoleCategory = 'GENERAL'")
    Optional<ClubRole> findDefaultGeneralRoleByClub(Club club);
}
