package com.mohaeng.infrastructure.authentication.jwt.service;

import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.infrastructure.authentication.jwt.exception.NotFoundAccessTokenException;
import com.mohaeng.infrastructure.authentication.jwt.usecase.ExtractAccessTokenUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ExtractAccessToken 은 ")
class ExtractTokenTest {

    private static final String TOKEN_TYPE = "Bearer ";
    private final ExtractAccessTokenUseCase extractAccessTokenUseCase = new ExtractAccessToken();

    @Test
    @DisplayName("토큰이 Bearer로 시작하지 않으면 예외를 발생시킨다.")
    void throwExceptionCauseByTokenNotStartedBearer() {
        assertThatThrownBy(
                () -> extractAccessTokenUseCase.command(
                        new ExtractAccessTokenUseCase.Command("aa.bb.cc")
                )).isInstanceOf(NotFoundAccessTokenException.class);
    }

    @Test
    @DisplayName("토큰이 올바르지 않은 형식일 경우 예외를 발생시킨다.")
    void throwExceptionCauseByInvalidToken() {
        assertAll(
                () -> assertThatThrownBy(() -> extractAccessTokenUseCase.command(
                        new ExtractAccessTokenUseCase.Command(TOKEN_TYPE)
                )).isInstanceOf(NotFoundAccessTokenException.class),
                () -> assertThatThrownBy(() -> extractAccessTokenUseCase.command(
                        new ExtractAccessTokenUseCase.Command(TOKEN_TYPE + " ")
                )).isInstanceOf(NotFoundAccessTokenException.class)
        );
    }

    @Test
    @DisplayName("토큰이 정상적으로 존재한다면 해당 토큰을 반환한다.")
    void returnTokenWhenTokenCorrect() {
        AccessToken token = extractAccessTokenUseCase.command(
                new ExtractAccessTokenUseCase.Command(TOKEN_TYPE + "token")
        );

        assertThat(token.token()).isEqualTo("token");
    }
}