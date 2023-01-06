package com.mohaeng.authentication.domain.model;

import com.mohaeng.authentication.exception.NotFoundAccessTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AccessToken 은 ")
class AccessTokenTest {

    private static final String TOKEN_TYPE = "Bearer ";

    @Test
    @DisplayName("fromBearerTypeToken(token) 시 token이 null이면 예외를 발생시킨다.")
    void throwExceptionWhenFromBearerTypeTokenByNullToken() {
        assertThatThrownBy(() -> AccessToken.fromBearerTypeToken(null))
                .isInstanceOf(NotFoundAccessTokenException.class);
    }

    @Test
    @DisplayName("fromBearerTypeToken(token) 시 token이 Bearer로 시작하지 않으면 예외를 발생시킨다.")
    void throwExceptionWhenFromBearerTypeTokenByNotStartedBearer() {
        assertThatThrownBy(() -> AccessToken.fromBearerTypeToken("token"))
                .isInstanceOf(NotFoundAccessTokenException.class);
    }

    @Test
    @DisplayName("fromBearerTypeToken(token) 시 token이 올바른 경우 Bearer 을 제외한 해당 토큰만을 반환한다.")
    void returnRemoveTokenTypeByCorrectToken() {
        AccessToken token = AccessToken.fromBearerTypeToken(TOKEN_TYPE + "token");
        assertThat(token.token()).isEqualTo("token");
    }
}