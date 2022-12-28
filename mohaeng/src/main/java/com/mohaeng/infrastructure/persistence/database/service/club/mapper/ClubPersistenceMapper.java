package com.mohaeng.infrastructure.persistence.database.service.club.mapper;

import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubRoleJpaEntity;

public class ClubPersistenceMapper {

    public static ClubJpaEntity toJpaEntity(final Club club) {
        ClubJpaEntity clubJpaEntity = new ClubJpaEntity(
                club.name(),
                club.description(),
                club.maxPeopleCount()
        );
       club.clubRoles()
                .stream()
                .map(it -> new ClubRoleJpaEntity(it.roleCategory(), it.name(), it.isBasicRile()))
                .forEach(clubJpaEntity::addClubRole);
       return clubJpaEntity;
    }
}
