package com.mohaeng.authentication.presentation.interceptor;

import com.mohaeng.authentication.application.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.authentication.application.usecase.ExtractClaimsUseCase;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.authentication.exception.AuthenticationException;
import com.mohaeng.authentication.infrastructure.jwt.service.ExtractAccessToken;
import com.mohaeng.common.exception.BaseExceptionType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.INVALID_ACCESS_TOKEN;
import static com.mohaeng.authentication.exception.AuthenticationExceptionType.NOT_FOUND_ACCESS_TOKEN;
import static com.mohaeng.common.fixtures.AuthenticationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogInInterceptorTest {

    private final ExtractAccessTokenUseCase extractAccessTokenUseCase = new ExtractAccessToken();
    private final ExtractClaimsUseCase extractClaimsUseCase = mock(ExtractClaimsUseCase.class);
    private final AuthenticationContext authenticationContext = mock(AuthenticationContext.class);
    private final LogInInterceptor logInInterceptor = new LogInInterceptor(extractAccessTokenUseCase,
            extractClaimsUseCase, authenticationContext);

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    @DisplayName("Authorization 헤더에 값이 존재하지 않으면 예외를 발생시킨다.")
    void throwExceptionWhenNotExistAuthorizationHeader() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                () -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class))
        ).exceptionType();
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("토큰이 Bearer로 시작하지 않으면 예외를 발생시킨다.")
    void throwExceptionWhenTokenNotStartedBearer() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(ACCESS_TOKEN);

        BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                () -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class))
        ).exceptionType();
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("토큰의 클레임에 MEMBER_ID_CLAIM 이 없으면 예외를 발생시킨다.")
    void throwExceptionWhenTokenNotContainsMemberIdClaim() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_ACCESS_TOKEN);
        when(extractClaimsUseCase.command(any())).thenReturn(new Claims());

        BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                () -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class))
        ).exceptionType();
        assertThat(baseExceptionType).isEqualTo(INVALID_ACCESS_TOKEN);
    }
}