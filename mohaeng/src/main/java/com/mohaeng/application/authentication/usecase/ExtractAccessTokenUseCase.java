package com.mohaeng.application.authentication.usecase;

import com.mohaeng.domain.authentication.model.AccessToken;

public interface ExtractAccessTokenUseCase {

    AccessToken command(final Command command);

    record Command(
            String header
    ) {
    }
}
