package com.mohaeng.infrastructure.persistence.database.repository.club;

import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<ClubJpaEntity, Long> {
}
