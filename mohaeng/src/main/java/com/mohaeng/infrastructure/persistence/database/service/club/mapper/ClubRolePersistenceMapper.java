package com.mohaeng.infrastructure.persistence.database.service.club.mapper;

import com.mohaeng.domain.club.domain.ClubRole;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubRoleJpaEntity;

import java.util.List;

public class ClubRolePersistenceMapper {

    public static List<ClubRoleJpaEntity> toJpaEntities(final ClubJpaEntity clubJpaEntity,
                                                        final List<ClubRole> clubRoles) {
        return clubRoles.stream()
                .map(it -> toJpaEntity(clubJpaEntity, it))
                .toList();
    }

    public static ClubRoleJpaEntity toJpaEntity(final ClubJpaEntity clubJpaEntity, final ClubRole clubRole) {
        return new ClubRoleJpaEntity(
                clubRole.roleCategory(),
                clubRole.name(),
                clubRole.isBasicRile(),
                clubJpaEntity
        );
    }
}
