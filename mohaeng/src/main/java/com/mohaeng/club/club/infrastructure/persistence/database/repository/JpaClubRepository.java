package com.mohaeng.club.club.infrastructure.persistence.database.repository;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaClubRepository extends JpaRepository<Club, Long>, ClubRepository {
}
