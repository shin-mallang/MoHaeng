package com.mohaeng.member.application.usecase;

import com.mohaeng.member.domain.model.enums.Gender;

public interface SignUpUseCase {

    void command(final Command command);

    record Command(
            String username,
            String password,
            String name,
            int age,
            Gender gender
    ) {
    }
}
