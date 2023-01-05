package com.mohaeng.member.application.mapper;

import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.Member;

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
