package com.mohaeng.authentication.infrastructure.jwt.service;

import com.mohaeng.authentication.application.usecase.CreateTokenUseCase;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.common.fixtures.AuthenticationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateToken(토큰 생성) 은")
class CreateTokenTest {

    private final CreateTokenUseCase createTokenUseCase = new CreateToken(new AuthenticationFixture.MockJwtProperties());

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("Claims를 가지고 JWT를 생성한다.")
        void success_test_1() {
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
}