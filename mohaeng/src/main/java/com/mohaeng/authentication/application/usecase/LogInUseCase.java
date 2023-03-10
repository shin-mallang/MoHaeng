package com.mohaeng.authentication.application.usecase;

import com.mohaeng.authentication.domain.model.AccessToken;

public interface LogInUseCase {

    /**
     * 아이디와 비밀번호를 통해 로그인을 진행한다.
     *
     * @return 회원 식별자
     */
    AccessToken command(final Command command);

    record Command(
            String username,
            String password
    ) {
    }
}
