package com.mohaeng.presentation.config.authentication;

import com.mohaeng.presentation.api.authentication.interceptor.LogInInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {

    private final LogInInterceptor logInInterceptor;

    public AuthenticationConfig(final LogInInterceptor logInInterceptor) {
        this.logInInterceptor = logInInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(logInInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/**/sign-up", "/api/login");
    }
}