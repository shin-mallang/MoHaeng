package com.mohaeng.presentation.config.authentication;

import com.mohaeng.presentation.api.authentication.argumentresolver.AuthArgumentResolver;
import com.mohaeng.presentation.api.authentication.interceptor.LogInInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.mohaeng.presentation.api.authentication.AuthenticationRestController.LOGIN_URL;
import static com.mohaeng.presentation.api.member.SignUpController.SIGN_UP_URL;

@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {

    private final LogInInterceptor logInInterceptor;
    private final AuthArgumentResolver authArgumentResolver;

    public AuthenticationConfig(final LogInInterceptor logInInterceptor,
                                final AuthArgumentResolver authArgumentResolver) {
        this.logInInterceptor = logInInterceptor;
        this.authArgumentResolver = authArgumentResolver;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(logInInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api" + SIGN_UP_URL,
                        "/api" + LOGIN_URL);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}