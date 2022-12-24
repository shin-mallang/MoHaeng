package com.mohaeng.presentation.api.authentication.mapper;

import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.domain.authentication.usecase.LogInUseCase;
import com.mohaeng.presentation.api.authentication.request.LoginRequest;
import com.mohaeng.presentation.api.authentication.response.TokenResponse;

public class AuthenticationMapper {

    public static LogInUseCase.Command toLoginDto(final LoginRequest loginRequest) {
        return new LogInUseCase.Command(loginRequest.username(), loginRequest.password());
    }

    public static TokenResponse toResponseDto(final AccessToken token) {
        return new TokenResponse(token.token());
    }
}
