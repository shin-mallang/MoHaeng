package com.mohaeng.infrastructure.persistence.database.service.club;

import com.mohaeng.domain.club.domain.ClubQuery;
import com.mohaeng.infrastructure.persistence.database.repository.club.ClubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClubJpaQuery implements ClubQuery {

    private final ClubRepository clubRepository;

    public ClubJpaQuery(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }
}
