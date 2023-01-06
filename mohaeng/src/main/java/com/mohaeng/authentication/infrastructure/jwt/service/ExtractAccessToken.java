package com.mohaeng.authentication.infrastructure.jwt.service;

import com.mohaeng.authentication.application.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import org.springframework.stereotype.Component;

@Component
public class ExtractAccessToken implements ExtractAccessTokenUseCase {

    @Override
    public AccessToken command(final Command command) {
        return AccessToken.fromBearerTypeToken(command.header());
    }
}
