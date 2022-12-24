package com.mohaeng.infrastructure.authentication.jwt.service;

import com.mohaeng.common.jwt.Claims;
import com.mohaeng.common.properties.JwtProperties;
import com.mohaeng.domain.authentication.service.JwtService;
import com.mohaeng.infrastructure.authentication.jwt.exception.AccessTokenInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("JwtServiceImpl은 ")
class JwtServiceImplTest {

    private final JwtService jwtService = new JwtServiceImpl(new MockJwtProperties());

    @Test
    @DisplayName("Claims를 가지고 JWT를 생성한다.")
    void createJWTByClaims() {
        // given
        Claims claims = new Claims(new HashMap<>(){{
            put("key1", "value1");
            put("key2", "2");
        }});

        // when
        String accessToken = jwtService.createAccessToken(claims);

        // then
        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("토큰을 받으면 해당 토큰의 클레임을 반환한다.")
    void returnClaimsWhenGivenToken() {
        // given
        Claims claims = new Claims(new HashMap<>(){{
            put("key1", "value1");
            put("key2", "2");
        }});
        String accessToken = jwtService.createAccessToken(claims);

        // when
        Claims returnClaims = jwtService.getClaims(accessToken);

        // then
        assertAll(
                () -> assertThat(returnClaims.get("key1")).isEqualTo("value1"),
                () -> assertThat(returnClaims.get("key2")).isEqualTo("2")
        );
    }

    @Test
    @DisplayName("토큰이 올바르지 않다면 예외를 발생시킨다.")
    void throwExceptionWhenInvalidJWT() {
        // when, then
        assertThatThrownBy(() -> jwtService.getClaims("invalid token"))
                .isInstanceOf(AccessTokenInvalidException.class);
    }

    private static class MockJwtProperties extends JwtProperties {
        public MockJwtProperties() {
            super("7JWI64WV7ZWY7IS47JqU7KCA64qU7Iug64+Z7ZuI7J6F64uI64uk", 100L);
        }
    }
}