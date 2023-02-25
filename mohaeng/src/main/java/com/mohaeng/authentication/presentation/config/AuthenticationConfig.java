package com.mohaeng.authentication.presentation.config;

import com.mohaeng.authentication.presentation.argumentresolver.AuthArgumentResolver;
import com.mohaeng.authentication.presentation.interceptor.LogInInterceptor;
import com.mohaeng.common.presentation.interceptor.PathAndMethodMatcherInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Set;

import static com.mohaeng.authentication.presentation.LogInController.LOGIN_URL;
import static com.mohaeng.club.club.presentation.query.QueryClubByIdController.QUERY_CLUB_BY_ID_URL;
import static com.mohaeng.club.club.presentation.query.SearchClubController.SEARCH_CLUB_URL;
import static com.mohaeng.member.presentation.SignUpController.SIGN_UP_URL;
import static org.springframework.http.HttpMethod.GET;

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
        PathAndMethodMatcherInterceptor interceptor = new PathAndMethodMatcherInterceptor(logInInterceptor);
        interceptor
                .addPathPatterns(Set.of("/api/**"))
                .excludePathPattern(Set.of(SIGN_UP_URL, LOGIN_URL))
                .excludePathPattern(Set.of(QUERY_CLUB_BY_ID_URL, SEARCH_CLUB_URL), Set.of(GET));

        // 모든 경로에 대해 매핑된다
        registry.addInterceptor(interceptor);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}