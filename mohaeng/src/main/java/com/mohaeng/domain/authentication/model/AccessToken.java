package com.mohaeng.domain.authentication.model;

import com.mohaeng.domain.authentication.exception.NotFoundAccessTokenException;
import org.apache.logging.log4j.util.Strings;

public record AccessToken(
        String token
) {
    private static final String TOKEN_TYPE = "Bearer ";
    private static final String EMPTY = "";

    public static AccessToken fromTypeToken(final String token) {
        validateToken(token);
        return new AccessToken(token.replace(TOKEN_TYPE, EMPTY));
    }

    private static void validateToken(final String token) {
        if (token == null) {
            throw new NotFoundAccessTokenException();
        }
        if (!token.contains(TOKEN_TYPE)) {
            throw new NotFoundAccessTokenException();
        }
        String replacedToken = token.replace(TOKEN_TYPE, EMPTY);
        if (Strings.isBlank(replacedToken)) {
            throw new NotFoundAccessTokenException();
        }
    }
}
