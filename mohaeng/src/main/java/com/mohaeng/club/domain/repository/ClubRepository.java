package com.mohaeng.club.domain.repository;

import com.mohaeng.club.domain.model.Club;

import java.util.Optional;

public interface ClubRepository {

    Club save(final Club club);

    Optional<Club> findById(final Long id);

    void delete(final Club club);
}
