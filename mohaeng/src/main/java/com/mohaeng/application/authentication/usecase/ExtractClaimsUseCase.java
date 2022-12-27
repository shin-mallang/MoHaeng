package com.mohaeng.application.authentication.usecase;

import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.domain.authentication.domain.Claims;

public interface ExtractClaimsUseCase {

    Claims command(final Command command);

    record Command(
            AccessToken token
    ) {
    }
}
