package com.mohaeng.domain.member.service.mapper;

import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.domain.member.usecase.SignUpUseCase;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.service.member.dto.CreateMemberDto;

public class MemberDomainMapper {

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

    public static CreateMemberDto toPersistenceLayerDto(final SignUpUseCase.Command command) {
        return new CreateMemberDto(
                command.username(),
                command.password(),
                command.name(),
                command.age(),
                command.gender()
        );
    }
}
