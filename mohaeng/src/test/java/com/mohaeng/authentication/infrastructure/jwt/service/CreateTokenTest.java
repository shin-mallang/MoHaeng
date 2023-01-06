package com.mohaeng.authentication.infrastructure.jwt.service;

import com.mohaeng.authentication.application.usecase.CreateTokenUseCase;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.common.fixtures.AuthenticationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateToken 은 ")
class CreateTokenTest {

    private final CreateTokenUseCase createTokenUseCase = new CreateToken(new AuthenticationFixture.MockJwtProperties());

    @Test
    @DisplayName("Claims를 가지고 JWT를 생성한다.")
    void createJWTByClaims() {
        // given
        Claims claims = AuthenticationFixture.claims();

        // when
        String accessToken = createTokenUseCase.command(
                new CreateTokenUseCase.Command(claims)
        );

        // then
        assertThat(accessToken).isNotNull();
    }
}