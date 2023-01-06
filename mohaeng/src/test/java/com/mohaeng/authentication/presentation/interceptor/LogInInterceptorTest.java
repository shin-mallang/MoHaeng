package com.mohaeng.authentication.presentation.interceptor;

import com.mohaeng.authentication.application.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.authentication.application.usecase.ExtractClaimsUseCase;
import com.mohaeng.authentication.domain.exception.NotFoundAccessTokenException;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.authentication.infrastructure.jwt.service.ExtractAccessToken;
import com.mohaeng.authentication.infrastructure.jwt.service.exception.InvalidAccessTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.AuthenticationFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    /**
     * 1. Header에 값이 존재하지 않을 때
     * <p>
     * 2. Bearer로 시작하지 않을 땨
     * <p>
     * 3. 토큰의 클레임에 MEMBER_ID_CLAIM 이 없을 때
     * <p>
     * 모두 있다면 AuthenticationContext의 setContext에 MEMBER_ID_CLAIM 저장
     */
    @Test
    @DisplayName("Authorization 헤더에 값이 존재하지 않으면 예외를 발생시킨다.")
    void throwExceptionWhenNotExistAuthorizationHeader() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        assertThatThrownBy(() -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class)))
                .isInstanceOf(NotFoundAccessTokenException.class);
    }

    @Test
    @DisplayName("토큰이 Bearer로 시작하지 않으면 예외를 발생시킨다.")
    void throwExceptionWhenTokenNotStartedBearer() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(ACCESS_TOKEN);

        assertThatThrownBy(() -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class)))
                .isInstanceOf(NotFoundAccessTokenException.class);
    }

    @Test
    @DisplayName("토큰의 클레임에 MEMBER_ID_CLAIM 이 없으면 예외를 발생시킨다.")
    void throwExceptionWhenTokenNotContainsMemberIdClaim() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_ACCESS_TOKEN);
        when(extractClaimsUseCase.command(any())).thenReturn(new Claims());

        assertThatThrownBy(() -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class)))
                .isInstanceOf(InvalidAccessTokenException.class);
    }
}