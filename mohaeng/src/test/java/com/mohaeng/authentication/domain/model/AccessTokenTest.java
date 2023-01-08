package com.mohaeng.authentication.domain.model;

import com.mohaeng.authentication.exception.AuthenticationException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.NOT_FOUND_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AccessToken 은 ")
class AccessTokenTest {

    private static final String TOKEN_TYPE = "Bearer ";

    @Test
    @DisplayName("fromBearerTypeToken(token) 시 token이 null이면 예외를 발생시킨다.")
    void throwExceptionWhenFromBearerTypeTokenByNullToken() {
        BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                () -> AccessToken.fromBearerTypeToken(null))
                .exceptionType();
        Assertions.assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("fromBearerTypeToken(token) 시 token이 Bearer로 시작하지 않으면 예외를 발생시킨다.")
    void throwExceptionWhenFromBearerTypeTokenByNotStartedBearer() {
        BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                () -> AccessToken.fromBearerTypeToken("token"))
                .exceptionType();
        Assertions.assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("fromBearerTypeToken(token) 시 token이 올바른 경우 Bearer 을 제외한 해당 토큰만을 반환한다.")
    void returnRemoveTokenTypeByCorrectToken() {
        AccessToken token = AccessToken.fromBearerTypeToken(TOKEN_TYPE + "token");
        assertThat(token.token()).isEqualTo("token");
    }
}