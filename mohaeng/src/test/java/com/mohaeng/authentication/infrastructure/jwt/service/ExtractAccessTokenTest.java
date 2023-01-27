package com.mohaeng.authentication.infrastructure.jwt.service;

import com.mohaeng.authentication.application.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.exception.AuthenticationException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.NOT_FOUND_ACCESS_TOKEN;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ExtractAccessToken 은 ")
class ExtractAccessTokenTest {

    private final ExtractAccessTokenUseCase extractAccessTokenUseCase = new ExtractAccessToken();

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("토큰이 정상적으로 존재한다면 해당 토큰을 반환한다.")
        void success_test_1() {
            AccessToken token = extractAccessTokenUseCase.command(
                    new ExtractAccessTokenUseCase.Command(BEARER_TOKEN_TYPE + "token")
            );

            assertThat(token.token()).isEqualTo("token");
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("토큰이 Bearer로 시작하지 않으면 예외를 발생시킨다.")
        void fail_test_1() {
            BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                    () -> extractAccessTokenUseCase.command(
                            new ExtractAccessTokenUseCase.Command("aa.bb.cc")
                    )).exceptionType();
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("토큰이 올바르지 않은 형식일 경우 예외를 발생시킨다.")
        void fail_test_2() {
            assertAll(
                    () -> assertThat(assertThrows(AuthenticationException.class,
                            () -> extractAccessTokenUseCase.command(
                                    new ExtractAccessTokenUseCase.Command(BEARER_TOKEN_TYPE)
                            )).exceptionType()).isEqualTo(NOT_FOUND_ACCESS_TOKEN),

                    () -> assertThat(assertThrows(AuthenticationException.class,
                            () -> extractAccessTokenUseCase.command(
                                    new ExtractAccessTokenUseCase.Command(BEARER_TOKEN_TYPE + " ")
                            )).exceptionType()).isEqualTo(NOT_FOUND_ACCESS_TOKEN)
            );
        }
    }
}