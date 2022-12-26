package com.mohaeng.infrastructure.authentication.jwt.service;

import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.infrastructure.authentication.jwt.usecase.ExtractAccessTokenUseCase;
import org.springframework.stereotype.Component;

@Component
public class ExtractAccessToken implements ExtractAccessTokenUseCase {

    @Override
    public AccessToken command(final Command command) {
        return AccessToken.fromTypeToken(command.header());
    }
}
