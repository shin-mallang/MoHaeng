package com.mohaeng.infrastructure.persistence.database.repository.club.role;

import com.mohaeng.domain.club.model.role.ClubRole;
import com.mohaeng.domain.club.repository.role.ClubRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaClubRoleRepository extends JpaRepository<ClubRole, Long>, ClubRoleRepository {

    @Override
    default List<ClubRole> saveAll(List<ClubRole> entities) {
        return saveAll((Iterable<ClubRole>) entities);
    }
}
