package com.mohaeng.authentication.presentation.mapper;

import com.mohaeng.authentication.application.usecase.LogInUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.presentation.LogInController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.AuthenticationFixture.accessToken;
import static com.mohaeng.common.fixtures.AuthenticationFixture.loginRequest;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthenticationControllerMapper 는 ")
class AuthenticationControllerMapperTest {
    
    @DisplayName("LoginRequest -> LogInUseCase.Command로 변환한다.")
    @Test
    void loginRequestToLogInUseCaseCommand() {
        LogInController.LoginRequest loginRequest = loginRequest();
        LogInUseCase.Command command = AuthenticationControllerMapper.toApplicationLayerDto(loginRequest);

        Assertions.assertAll(
                () -> assertThat(command.username()).isEqualTo(loginRequest.username()),
                () -> assertThat(command.password()).isEqualTo(loginRequest.password())
        );
    }

    @DisplayName("AccessToken -> TokenResponse로 변환한다.")
    @Test
    void accessTokenToTokenResponse() {
        AccessToken accessToken = accessToken();
        LogInController.TokenResponse tokenResponse = AuthenticationControllerMapper.toResponseDto(accessToken);

        assertThat(accessToken.token()).isEqualTo(tokenResponse.token());
    }
}
