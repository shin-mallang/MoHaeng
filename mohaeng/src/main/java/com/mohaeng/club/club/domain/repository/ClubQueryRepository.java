package com.mohaeng.club.club.domain.repository;

import com.mohaeng.club.club.domain.model.Club;

import java.util.Optional;

public interface ClubQueryRepository {

    Optional<Club> findById(final Long id);
}
