package com.mohaeng.authentication.presentation.interceptor;

import com.mohaeng.authentication.application.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.authentication.application.usecase.ExtractClaimsUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.authentication.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.mohaeng.authentication.application.service.LogIn.MEMBER_ID_CLAIM;
import static com.mohaeng.authentication.exception.AuthenticationExceptionType.INVALID_ACCESS_TOKEN;
import static java.lang.Long.parseLong;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ExtractAccessTokenUseCase extractAccessTokenUseCase;
    private final ExtractClaimsUseCase extractClaimsUseCase;
    private final AuthenticationContext authenticationContext;

    public AuthInterceptor(final ExtractAccessTokenUseCase extractAccessTokenUseCase,
                           final ExtractClaimsUseCase extractClaimsUseCase,
                           final AuthenticationContext authenticationContext) {
        this.extractAccessTokenUseCase = extractAccessTokenUseCase;
        this.extractClaimsUseCase = extractClaimsUseCase;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {
        AccessToken token = extractAccessTokenUseCase.command(
                new ExtractAccessTokenUseCase.Command(request.getHeader(HttpHeaders.AUTHORIZATION))
        );
        Claims claims = extractClaimsUseCase.command(
                new ExtractClaimsUseCase.Command(token)
        );
        validateClaims(claims);
        authenticationContext.setPrincipal(parseLong(claims.get(MEMBER_ID_CLAIM)));
        return true;
    }

    private void validateClaims(final Claims claims) {
        // 클레임이 있는지 검사
        if (claims.get(MEMBER_ID_CLAIM) == null) {
            throw new AuthenticationException(INVALID_ACCESS_TOKEN);
        }
        // 클레임이 Long으로 파싱될 수 있는지 검사
        try {
            parseLong(claims.get(MEMBER_ID_CLAIM));
        } catch (NumberFormatException e) {
            throw new AuthenticationException(INVALID_ACCESS_TOKEN);
        }
    }
}
