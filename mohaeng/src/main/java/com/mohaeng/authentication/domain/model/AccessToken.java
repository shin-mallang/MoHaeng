package com.mohaeng.authentication.domain.model;

import com.mohaeng.authentication.exception.AuthenticationException;
import org.apache.logging.log4j.util.Strings;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.NOT_FOUND_ACCESS_TOKEN;

public record AccessToken(
        String token
) {
    private static final String BEARER_TOKEN_TYPE = "Bearer ";
    private static final String EMPTY = "";

    public static AccessToken fromBearerTypeToken(final String token) {
        validateToken(token);
        return new AccessToken(token.replace(BEARER_TOKEN_TYPE, EMPTY));
    }

    private static void validateToken(final String token) {
        if (token == null) {
            throw new AuthenticationException(NOT_FOUND_ACCESS_TOKEN);
        }
        if (!token.contains(BEARER_TOKEN_TYPE)) {
            throw new AuthenticationException(NOT_FOUND_ACCESS_TOKEN);
        }
        String replacedToken = token.replace(BEARER_TOKEN_TYPE, EMPTY);
        if (Strings.isBlank(replacedToken)) {
            throw new AuthenticationException(NOT_FOUND_ACCESS_TOKEN);
        }
    }
}
