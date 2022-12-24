package com.mohaeng.domain.authentication.domain;

public record AccessToken(
        String token,
        Long memberId
) {
}
