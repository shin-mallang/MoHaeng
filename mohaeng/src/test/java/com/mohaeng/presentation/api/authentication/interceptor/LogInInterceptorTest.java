package com.mohaeng.presentation.api.authentication.interceptor;

import com.mohaeng.application.authentication.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.application.authentication.usecase.ExtractClaimsUseCase;
import com.mohaeng.domain.authentication.model.Claims;
import com.mohaeng.domain.authentication.exception.NotFoundAccessTokenException;
import com.mohaeng.infrastructure.authentication.jwt.service.ExtractAccessToken;
import com.mohaeng.infrastructure.authentication.jwt.service.exception.InvalidAccessTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogInInterceptorTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";

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
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("token");

        assertThatThrownBy(() -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class)))
                .isInstanceOf(NotFoundAccessTokenException.class);
    }

    @Test
    @DisplayName("토큰의 클레임에 MEMBER_ID_CLAIM 이 없으면 예외를 발생시킨다.")
    void throwExceptionWhenTokenNotContainsMemberIdClaim() {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer token");
        when(extractClaimsUseCase.command(any())).thenReturn(new Claims());

        assertThatThrownBy(() -> logInInterceptor.preHandle(request, mock(HttpServletResponse.class), mock(Object.class)))
                .isInstanceOf(InvalidAccessTokenException.class);
    }
}