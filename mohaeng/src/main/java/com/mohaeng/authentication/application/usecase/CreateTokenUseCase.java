package com.mohaeng.authentication.application.usecase;

import com.mohaeng.authentication.domain.model.Claims;

public interface CreateTokenUseCase {

    String command(final Command command);

    record Command(
            Claims claims
    ) {
    }
}
