package com.mohaeng.authentication.infrastructure.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("jwt")
public class JwtProperties {

    private String secretKey;
    private Long accessTokenExpirationPeriodDay;

    @ConstructorBinding
    public JwtProperties(final String secretKey, final Long accessTokenExpirationPeriodDay) {
        this.secretKey = secretKey;
        this.accessTokenExpirationPeriodDay = accessTokenExpirationPeriodDay;
    }

    public String secretKey() {
        return secretKey;
    }

    public Long accessTokenExpirationPeriodDay() {
        return accessTokenExpirationPeriodDay;
    }
}
