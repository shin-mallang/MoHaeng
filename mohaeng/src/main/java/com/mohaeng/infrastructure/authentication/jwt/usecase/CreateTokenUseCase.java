package com.mohaeng.infrastructure.authentication.jwt.usecase;

import com.mohaeng.common.jwt.Claims;

public interface CreateTokenUseCase {

    String command(final Command command);

    record Command(
            Claims claims
    ) {
    }
}
