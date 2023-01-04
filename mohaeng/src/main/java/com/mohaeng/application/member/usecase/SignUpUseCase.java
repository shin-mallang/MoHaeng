package com.mohaeng.application.member.usecase;

import com.mohaeng.domain.member.model.enums.Gender;

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
