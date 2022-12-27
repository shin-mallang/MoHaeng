package com.mohaeng.application.authentication.usecase;

import com.mohaeng.domain.authentication.domain.Claims;

public interface CreateTokenUseCase {

    String command(final Command command);

    record Command(
            Claims claims
    ) {
    }
}
