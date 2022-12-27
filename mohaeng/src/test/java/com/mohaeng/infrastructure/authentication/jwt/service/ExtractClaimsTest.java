package com.mohaeng.infrastructure.authentication.jwt.service;

import com.mohaeng.application.authentication.usecase.ExtractClaimsUseCase;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.domain.authentication.domain.Claims;
import com.mohaeng.infrastructure.authentication.jwt.config.JwtProperties;
import com.mohaeng.infrastructure.authentication.jwt.service.exception.InvalidAccessTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ExtractClaims 은 ")
class ExtractClaimsTest {

    private static final String TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2ODA2ODI4NTEsIm1lbWJlcklkIjoiMSJ9.s2E4VEA_w16a9Z9QxCSDtq8DNHD-VgRLRKluMA1frxZBEt6WERbrkAlNLYybF4-IH6s4Ogei52zSpEBq_LG9-g";

    private final ExtractClaimsUseCase extractClaimsUseCase = new ExtractClaims(new MockJwtProperties());

    @Test
    @DisplayName("토큰을 받으면 해당 토큰의 클레임을 반환한다.")
    void returnClaimsWhenGivenToken() {
        // given
        Claims returnClaims = extractClaimsUseCase.command(
                new ExtractClaimsUseCase.Command(new AccessToken(TOKEN))
        );

        // then
        assertAll(
                () -> assertThat(returnClaims.claims()).isNotEmpty()
        );
    }

    @Test
    @DisplayName("토큰이 올바르지 않다면 예외를 발생시킨다.")
    void throwExceptionWhenInvalidJWT() {
        // when, then
        assertThatThrownBy(() -> extractClaimsUseCase.command(
                        new ExtractClaimsUseCase.Command(new AccessToken("invalid token"))
                )
        ).isInstanceOf(InvalidAccessTokenException.class);
    }

    private static class MockJwtProperties extends JwtProperties {
        public MockJwtProperties() {
            super("7JWI64WV7ZWY7IS47JqU7KCA64qU7Iug64+Z7ZuI7J6F64uI64uk", 100L);
        }
    }
}