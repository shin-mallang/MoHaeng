package com.mohaeng.presentation.api.authentication.mapper;

import com.mohaeng.application.authentication.usecase.LogInUseCase;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.presentation.api.authentication.request.LoginRequest;
import com.mohaeng.presentation.api.authentication.response.TokenResponse;

public class AuthenticationControllerMapper {

    public static LogInUseCase.Command toApplicationLayerDto(final LoginRequest loginRequest) {
        return new LogInUseCase.Command(loginRequest.username(), loginRequest.password());
    }

    public static TokenResponse toResponseDto(final AccessToken token) {
        return new TokenResponse(token.token());
    }
}
