package com.mohaeng.presentation.api.authentication.mapper;

import com.mohaeng.application.authentication.usecase.LogInUseCase;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.presentation.api.authentication.LogInController;

public class AuthenticationControllerMapper {

    public static LogInUseCase.Command toApplicationLayerDto(final LogInController.LoginRequest loginRequest) {
        return new LogInUseCase.Command(loginRequest.username(), loginRequest.password());
    }

    public static LogInController.TokenResponse toResponseDto(final AccessToken token) {
        return new LogInController.TokenResponse(token.token());
    }
}
