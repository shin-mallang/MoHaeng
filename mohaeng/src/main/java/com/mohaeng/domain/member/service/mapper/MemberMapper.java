package com.mohaeng.domain.member.service.mapper;

import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;

public class MemberMapper {

    public static Member toDomainEntity(final MemberJpaEntity entity) {
        return new Member(
                entity.id(),
                entity.createdAt(),
                entity.lastModifiedAt(),
                entity.username(),
                entity.password(),
                entity.name(),
                entity.age(),
                entity.gender()
        );
    }
}
