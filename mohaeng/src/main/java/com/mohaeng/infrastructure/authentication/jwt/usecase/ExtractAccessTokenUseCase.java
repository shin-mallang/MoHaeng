package com.mohaeng.infrastructure.authentication.jwt.usecase;

import com.mohaeng.domain.authentication.domain.AccessToken;

public interface ExtractAccessTokenUseCase {

    AccessToken command(final Command command);

    record Command(
            String header
    ) {
    }
}
