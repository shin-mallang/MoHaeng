package com.mohaeng.application.member.mapper;

import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.domain.member.model.Member;

public class MemberApplicationMapper {

    public static Member toDomainEntity(SignUpUseCase.Command command) {
        return new Member(
                command.username(),
                command.password(),
                command.name(),
                command.age(),
                command.gender()
        );
    }
}
