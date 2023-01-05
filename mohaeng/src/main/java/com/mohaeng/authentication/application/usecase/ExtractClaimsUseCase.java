package com.mohaeng.authentication.application.usecase;

import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.domain.model.Claims;

public interface ExtractClaimsUseCase {

    Claims command(final Command command);

    record Command(
            AccessToken token
    ) {
    }
}
