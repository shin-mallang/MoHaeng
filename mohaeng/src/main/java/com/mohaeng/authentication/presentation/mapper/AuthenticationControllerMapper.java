package com.mohaeng.authentication.presentation.mapper;

import com.mohaeng.authentication.application.usecase.LogInUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.presentation.LogInController;

public class AuthenticationControllerMapper {

    public static LogInUseCase.Command toApplicationLayerDto(final LogInController.LoginRequest loginRequest) {
        return new LogInUseCase.Command(loginRequest.username(), loginRequest.password());
    }

    public static LogInController.TokenResponse toResponseDto(final AccessToken token) {
        return new LogInController.TokenResponse(token.token());
    }
}
