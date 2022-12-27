package com.mohaeng.application.member.mapper;

import com.mohaeng.application.member.dto.CreateMemberDto;
import com.mohaeng.application.member.usecase.SignUpUseCase;

public class MemberApplicationMapper {

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
