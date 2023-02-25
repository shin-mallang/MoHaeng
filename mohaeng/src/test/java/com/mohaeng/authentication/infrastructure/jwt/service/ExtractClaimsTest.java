package com.mohaeng.authentication.infrastructure.jwt.service;

import com.mohaeng.authentication.application.usecase.ExtractClaimsUseCase;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.authentication.exception.AuthenticationException;
import com.mohaeng.common.fixtures.AuthenticationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.INVALID_ACCESS_TOKEN;
import static com.mohaeng.common.fixtures.AuthenticationFixture.accessToken;
import static com.mohaeng.common.fixtures.AuthenticationFixture.invalidAccessToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ExtractClaims(Claims 추출) 은")
class ExtractClaimsTest {

    private final ExtractClaimsUseCase extractClaimsUseCase = new ExtractClaims(new AuthenticationFixture.MockJwtProperties());

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("토큰을 받으면 해당 토큰의 클레임을 반환한다.")
        void success_test_1() {
            // given
            Claims returnClaims = extractClaimsUseCase.command(
                    new ExtractClaimsUseCase.Command(accessToken())
            );

            // then
            assertAll(
                    () -> assertThat(returnClaims.claims()).isNotEmpty()
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {
        @Test
        @DisplayName("토큰이 올바르지 않다면 예외를 발생시킨다.")
        void fail_test_1() {
            // when, then
            org.assertj.core.api.Assertions.assertThat(assertThrows(AuthenticationException.class,
                    () -> extractClaimsUseCase.command(
                            new ExtractClaimsUseCase.Command(invalidAccessToken())
                    )).exceptionType()).isEqualTo(INVALID_ACCESS_TOKEN);
        }
    }
}