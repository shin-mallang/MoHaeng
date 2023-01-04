package com.mohaeng.application.authentication.usecase;

import com.mohaeng.domain.authentication.model.Claims;

public interface CreateTokenUseCase {

    String command(final Command command);

    record Command(
            Claims claims
    ) {
    }
}
