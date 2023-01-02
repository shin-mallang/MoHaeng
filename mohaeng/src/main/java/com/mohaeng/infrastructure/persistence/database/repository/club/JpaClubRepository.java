package com.mohaeng.infrastructure.persistence.database.repository.club;

import com.mohaeng.domain.club.Club;
import com.mohaeng.domain.club.ClubRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaClubRepository extends JpaRepository<Club, Long>, ClubRepository {
}
