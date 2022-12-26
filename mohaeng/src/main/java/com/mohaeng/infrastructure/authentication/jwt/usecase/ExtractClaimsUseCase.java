package com.mohaeng.infrastructure.authentication.jwt.usecase;

import com.mohaeng.common.jwt.Claims;

public interface ExtractClaimsUseCase {

    Claims command(final Command command);

    record Command(
            String token
    ) {
    }
}
