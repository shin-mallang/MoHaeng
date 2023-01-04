package com.mohaeng.infrastructure.persistence.database.repository.club;

import com.mohaeng.domain.club.model.Club;
import com.mohaeng.domain.club.repository.ClubRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaClubRepository extends JpaRepository<Club, Long>, ClubRepository {
}
