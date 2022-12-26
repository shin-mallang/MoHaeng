package com.mohaeng.infrastructure.persistence.database.service.member.mapper;

import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.service.member.dto.CreateMemberDto;

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
}
