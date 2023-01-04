package com.mohaeng.application.authentication.usecase;

import com.mohaeng.domain.authentication.model.AccessToken;
import com.mohaeng.domain.authentication.model.Claims;

public interface ExtractClaimsUseCase {

    Claims command(final Command command);

    record Command(
            AccessToken token
    ) {
    }
}
