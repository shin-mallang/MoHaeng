package com.mohaeng.common.fixtures;

import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.authentication.infrastructure.jwt.config.JwtProperties;
import com.mohaeng.authentication.presentation.LogInController;

import java.util.HashMap;

import static com.mohaeng.common.fixtures.MemberFixture.MALLANG_PASSWORD;
import static com.mohaeng.common.fixtures.MemberFixture.MALLANG_USERNAME;

public class AuthenticationFixture {

    public static final String KEY = "KEY";
    public static final String VALUE = "VALUE";

    public static final String BEARER_TOKEN_TYPE = "Bearer ";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String ACCESS_TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2ODA2ODI4NTEsIm1lbWJlcklkIjoiMSJ9.s2E4VEA_w16a9Z9QxCSDtq8DNHD-VgRLRKluMA1frxZBEt6WERbrkAlNLYybF4-IH6s4Ogei52zSpEBq_LG9-g";

    public static final String BEARER_ACCESS_TOKEN =
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.xxx.s2E4VEA_w16a9Z9QxCSDtq8DNHD-VgRLRKluMA1frxZBEt6WERbrkAlNLYybF4-IH6s4Ogei52zSpEBq_LG9-g";

    public static class MockJwtProperties extends JwtProperties {
        public MockJwtProperties() {
            super("7JWI64WV7ZWY7IS47JqU7KCA64qU7Iug64+Z7ZuI7J6F64uI64uk", 100L);
        }
    }

    public static Claims claims() {
        return new Claims(new HashMap<>() {{
            put(KEY, VALUE);
        }});
    }

    public static AccessToken accessToken() {
        return new AccessToken(ACCESS_TOKEN);
    }

    public static AccessToken invalidAccessToken() {
        return new AccessToken("INVALID");
    }

    public static LogInController.LoginRequest loginRequest() {
        return new LogInController.LoginRequest(MALLANG_USERNAME, MALLANG_PASSWORD);
    }
}
