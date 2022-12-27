package com.mohaeng.infrastructure.persistence.database.service.club;

import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.domain.club.domain.ClubCommand;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.club.ClubRepository;
import com.mohaeng.infrastructure.persistence.database.service.club.mapper.ClubPersistenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClubJpaCommand implements ClubCommand {

    private final ClubRepository clubRepository;

    public ClubJpaCommand(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public Long save(final Club club) {
        ClubJpaEntity clubJpaEntity = ClubPersistenceMapper.toJpaEntity(club);
        return clubRepository.save(clubJpaEntity).id();
    }
}
