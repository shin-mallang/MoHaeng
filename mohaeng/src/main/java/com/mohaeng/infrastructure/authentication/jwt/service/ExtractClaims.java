package com.mohaeng.infrastructure.authentication.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mohaeng.common.jwt.Claims;
import com.mohaeng.common.properties.JwtProperties;
import com.mohaeng.infrastructure.authentication.jwt.exception.AccessTokenInvalidException;
import com.mohaeng.infrastructure.authentication.jwt.usecase.ExtractClaimsUseCase;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class ExtractClaims implements ExtractClaimsUseCase {

    private final Algorithm algorithm;

    public ExtractClaims(final JwtProperties jwtProperties) {
        this.algorithm = Algorithm.HMAC512(jwtProperties.secretKey());
    }

    @Override
    public Claims command(Command command) {
        DecodedJWT jwt = decodeJwt(command.token());
        return toClaims(jwt);
    }

    /**
     * JWT 디코딩
     */
    private DecodedJWT decodeJwt(final String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new AccessTokenInvalidException();
        }
    }

    /**
     * JWT를 Claims로 매핑
     */
    private static Claims toClaims(final DecodedJWT jwt) {
        Map<String, String> collect = jwt.getClaims()
                .keySet()
                .stream()
                .collect(
                        toUnmodifiableMap(
                                key -> key,
                                key -> jwt.getClaim(key).toString().replace("\"", ""))
                );
        return new Claims(collect);
    }
}
