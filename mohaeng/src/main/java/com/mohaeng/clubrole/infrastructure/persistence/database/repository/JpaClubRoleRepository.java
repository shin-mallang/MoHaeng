package com.mohaeng.clubrole.infrastructure.persistence.database.repository;

import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaClubRoleRepository extends JpaRepository<ClubRole, Long>, ClubRoleRepository {

    @Override
    default List<ClubRole> saveAll(List<ClubRole> entities) {
        return saveAll((Iterable<ClubRole>) entities);
    }
}
