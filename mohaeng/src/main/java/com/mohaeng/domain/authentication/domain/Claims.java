package com.mohaeng.domain.authentication.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class Claims {

    private final Map<String, String> claims;

    public Claims() {
        this.claims = new HashMap<>();
    }

    public Claims(final Map<String, String> claims) {
        this.claims = Collections.unmodifiableMap(claims);
    }

    public void addClaims(final String name, final String value) {
        claims.put(name, value);
    }

    public Map<String, String> claims() {
        return unmodifiableMap(claims);
    }

    public String get(final String key) {
        return claims.get(key);
    }
}
