package com.mohaeng.domain.member.usecase;

import com.mohaeng.common.member.Gender;

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
