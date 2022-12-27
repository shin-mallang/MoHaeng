package com.mohaeng.infrastructure.persistence.database.service.member.mapper;

import com.mohaeng.application.member.dto.CreateMemberDto;
import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;

public class MemberPersistenceMapper {

    public static MemberJpaEntity toJpaEntity(final CreateMemberDto createMemberDto) {
        return new MemberJpaEntity(
                createMemberDto.username(),
                createMemberDto.password(),
                createMemberDto.name(),
                createMemberDto.age(),
                createMemberDto.gender()
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
