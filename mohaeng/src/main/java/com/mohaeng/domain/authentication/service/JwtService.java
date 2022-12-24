package com.mohaeng.domain.authentication.service;

import com.mohaeng.common.jwt.Claims;

public interface JwtService {

    String createAccessToken(final Claims claims);

    Claims getClaims(final String token);
}
