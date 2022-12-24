package com.mohaeng.infrastructure.authentication.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mohaeng.common.jwt.Claims;
import com.mohaeng.common.properties.JwtProperties;
import com.mohaeng.domain.authentication.service.JwtService;
import com.mohaeng.infrastructure.authentication.jwt.exception.AccessTokenInvalidException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class JwtServiceImpl implements JwtService {

    private final Algorithm algorithm;
    private final long accessTokenExpirationPeriodDayToMills;

    public JwtServiceImpl(final JwtProperties jwtProperties) {
        this.algorithm = Algorithm.HMAC512(jwtProperties.secretKey());
        this.accessTokenExpirationPeriodDayToMills =
                MILLISECONDS.convert(jwtProperties.accessTokenExpirationPeriodDay(), DAYS);
    }

    /**
     * Access Token 생성
     */
    @Override
    public String createAccessToken(final Claims claims) {
        // 만료일 지정
        JWTCreator.Builder builder = defaultJwtBuilderWithExpire();

        // Claim 세팅
        fillClaims(claims, builder);

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

    /**
     * Token 으로부터 Claim들 추출
     */
    @Override
    public Claims getClaims(final String token) {
        DecodedJWT jwt = decodeJwt(token);
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
