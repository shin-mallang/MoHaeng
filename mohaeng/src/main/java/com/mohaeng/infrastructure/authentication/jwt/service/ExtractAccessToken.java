package com.mohaeng.infrastructure.authentication.jwt.service;

import com.mohaeng.application.authentication.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.domain.authentication.model.AccessToken;
import org.springframework.stereotype.Component;

@Component
public class ExtractAccessToken implements ExtractAccessTokenUseCase {

    @Override
    public AccessToken command(final Command command) {
        return AccessToken.fromTypeToken(command.header());
    }
}
