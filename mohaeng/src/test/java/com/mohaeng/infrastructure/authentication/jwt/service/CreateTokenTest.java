package com.mohaeng.infrastructure.authentication.jwt.service;

import com.mohaeng.common.jwt.Claims;
import com.mohaeng.infrastructure.authentication.jwt.usecase.CreateTokenUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateToken 은 ")
class CreateTokenTest {

    private final CreateTokenUseCase createTokenUseCase = new CreateToken(new MockJwtProperties());

    @Test
    @DisplayName("Claims를 가지고 JWT를 생성한다.")
    void createJWTByClaims() {
        // given
        Claims claims = new Claims(new HashMap<>() {{
            put("key1", "value1");
            put("key2", "2");
        }});

        // when
        String accessToken = createTokenUseCase.command(
                new CreateTokenUseCase.Command(claims)
        );

        // then
        assertThat(accessToken).isNotNull();
    }
}