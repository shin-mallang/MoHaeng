package com.mohaeng.infrastructure.authentication.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.mohaeng.application.authentication.usecase.CreateTokenUseCase;
import com.mohaeng.domain.authentication.model.Claims;
import com.mohaeng.infrastructure.authentication.jwt.config.JwtProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class CreateToken implements CreateTokenUseCase {

    private final Algorithm algorithm;
    private final long accessTokenExpirationPeriodDayToMills;

    public CreateToken(final JwtProperties jwtProperties) {
        this.algorithm = Algorithm.HMAC512(jwtProperties.secretKey());
        this.accessTokenExpirationPeriodDayToMills =
                MILLISECONDS.convert(jwtProperties.accessTokenExpirationPeriodDay(), DAYS);
    }

    @Override
    public String command(final Command command) {
        // 만료일 지정
        JWTCreator.Builder builder = defaultJwtBuilderWithExpire();

        // Claim 세팅
        fillClaims(command.claims(), builder);

        return builder.sign(algorithm);
    }

    /**
     * 만료일 설정한 JWT Builder 반환
     */
    private JWTCreator.Builder defaultJwtBuilderWithExpire() {
        return JWT.create()
                .withExpiresAt(
                        new Date(accessTokenExpirationPeriodDayToMills + System.currentTimeMillis())
                );
    }

    /**
     * claim 채우기
     */
    private void fillClaims(final Claims claims, final JWTCreator.Builder builder) {
        claims.claims()
                .keySet()
                .forEach(key -> builder.withClaim(key, claims.get(key)));
    }
}
