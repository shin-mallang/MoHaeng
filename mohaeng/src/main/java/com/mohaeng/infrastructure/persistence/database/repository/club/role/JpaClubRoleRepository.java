package com.mohaeng.infrastructure.persistence.database.repository.club.role;

import com.mohaeng.domain.club.model.role.ClubRole;
import com.mohaeng.domain.club.repository.role.ClubRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaClubRoleRepository extends JpaRepository<ClubRole, Long>, ClubRoleRepository {
}
