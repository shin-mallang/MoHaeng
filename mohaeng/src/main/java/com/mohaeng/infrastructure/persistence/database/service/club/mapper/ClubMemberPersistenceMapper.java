package com.mohaeng.infrastructure.persistence.database.service.club.mapper;

import com.mohaeng.domain.club.domain.ClubMember;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubMemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubRoleJpaEntity;

import java.util.List;

public class ClubMemberPersistenceMapper {

    public static List<ClubMemberJpaEntity> toJpaEntities(final List<ClubRoleJpaEntity> clubRoleJpaEntities, final List<ClubMember> clubMembers) {
        return clubMembers.stream()
                .map(it -> toJpaEntity(clubRoleJpaEntities, it))
                .toList();
    }

    private static ClubMemberJpaEntity toJpaEntity(final List<ClubRoleJpaEntity> clubRoleJpaEntities, final ClubMember clubMember) {
        ClubRoleJpaEntity clubRoleJpaEntity = clubRoleJpaEntities.stream()
                .filter(role -> clubMember.clubRole().name().equals(role.name()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("발생하면 안되는 오류"));
        return new ClubMemberJpaEntity(clubRoleJpaEntity);
    }
}
