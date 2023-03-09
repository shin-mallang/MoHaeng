package com.mohaeng.club.club.domain.repository;

import com.mohaeng.club.club.domain.model.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClubQueryRepository {

    Optional<Club> findById(final Long id);

    Page<Club> findAllBySearchCond(final ClubSearchCond clubSearchCond, final Pageable pageable);

    Page<Club> findAllByMemberId(final Long memberId, final Pageable pageable);

    record ClubSearchCond(
            String name
    ) {
    }
}
