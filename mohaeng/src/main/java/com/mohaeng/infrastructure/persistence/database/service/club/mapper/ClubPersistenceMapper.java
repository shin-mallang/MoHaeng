package com.mohaeng.infrastructure.persistence.database.service.club.mapper;

import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;

public class ClubPersistenceMapper {

    public static ClubJpaEntity toJpaEntity(final Club club) {
        return new ClubJpaEntity(
                club.name(),
                club.description(),
                club.maxPeopleCount()
        );
    }
}
