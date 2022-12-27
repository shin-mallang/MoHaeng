package com.mohaeng.infrastructure.persistence.database.service.member.mapper;

import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;

public class MemberPersistenceMapper {

    public static MemberJpaEntity toJpaEntity(final Member member) {
        return new MemberJpaEntity(
                member.username(),
                member.password(),
                member.name(),
                member.age(),
                member.gender()
        );
    }

    public static Member toDomainEntity(MemberJpaEntity memberJpaEntity) {
        return new Member(
                memberJpaEntity.id(),
                memberJpaEntity.createdAt(),
                memberJpaEntity.lastModifiedAt(),
                memberJpaEntity.username(),
                memberJpaEntity.password(),
                memberJpaEntity.name(),
                memberJpaEntity.age(),
                memberJpaEntity.gender()
        );
    }
}
