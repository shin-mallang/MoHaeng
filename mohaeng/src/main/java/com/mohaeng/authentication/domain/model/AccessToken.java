package com.mohaeng.authentication.domain.model;

import com.mohaeng.authentication.domain.exception.NotFoundAccessTokenException;
import org.apache.logging.log4j.util.Strings;

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
            throw new NotFoundAccessTokenException();
        }
        if (!token.contains(BEARER_TOKEN_TYPE)) {
            throw new NotFoundAccessTokenException();
        }
        String replacedToken = token.replace(BEARER_TOKEN_TYPE, EMPTY);
        if (Strings.isBlank(replacedToken)) {
            throw new NotFoundAccessTokenException();
        }
    }
}
