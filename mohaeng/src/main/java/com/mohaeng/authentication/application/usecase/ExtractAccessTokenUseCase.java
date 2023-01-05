package com.mohaeng.authentication.application.usecase;

import com.mohaeng.authentication.domain.model.AccessToken;

public interface ExtractAccessTokenUseCase {

    AccessToken command(final Command command);

    record Command(
            String header
    ) {
    }
}
