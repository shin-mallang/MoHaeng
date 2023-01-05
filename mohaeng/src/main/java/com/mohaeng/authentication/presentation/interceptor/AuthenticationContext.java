package com.mohaeng.authentication.presentation.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthenticationContext {

    private Long principal;

    public Long principal() {
        return principal;
    }

    public void setPrincipal(final Long principal) {
        this.principal = principal;
    }
}