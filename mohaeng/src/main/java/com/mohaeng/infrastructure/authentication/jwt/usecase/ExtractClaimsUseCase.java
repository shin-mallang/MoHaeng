package com.mohaeng.infrastructure.authentication.jwt.usecase;

import com.mohaeng.common.jwt.Claims;
import com.mohaeng.domain.authentication.domain.AccessToken;

public interface ExtractClaimsUseCase {

    Claims command(final Command command);

    record Command(
            AccessToken token
    ) {
    }
}
