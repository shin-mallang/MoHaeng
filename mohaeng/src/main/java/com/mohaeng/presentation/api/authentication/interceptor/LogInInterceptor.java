package com.mohaeng.presentation.api.authentication.interceptor;

import com.mohaeng.application.authentication.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.application.authentication.usecase.ExtractClaimsUseCase;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.domain.authentication.domain.Claims;
import com.mohaeng.infrastructure.authentication.jwt.service.exception.InvalidAccessTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.mohaeng.application.authentication.service.LogIn.MEMBER_ID_CLAIM;
import static java.lang.Long.parseLong;

@Component
public class LogInInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final ExtractAccessTokenUseCase extractAccessTokenUseCase;
    private final ExtractClaimsUseCase extractClaimsUseCase;
    private final AuthenticationContext authenticationContext;

    public LogInInterceptor(final ExtractAccessTokenUseCase extractAccessTokenUseCase,
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
                new ExtractAccessTokenUseCase.Command(request.getHeader(AUTHORIZATION_HEADER))
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
            throw new InvalidAccessTokenException();
        }
        // 클레임이 Long으로 파싱될 수 있는지 검사
        try {
            parseLong(claims.get(MEMBER_ID_CLAIM));
        } catch (NumberFormatException e) {
            throw new InvalidAccessTokenException();
        }
    }
}
