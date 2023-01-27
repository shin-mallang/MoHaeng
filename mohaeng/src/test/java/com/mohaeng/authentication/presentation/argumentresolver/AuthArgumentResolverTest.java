package com.mohaeng.authentication.presentation.argumentresolver;

import com.mohaeng.authentication.presentation.interceptor.AuthenticationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("AuthArgumentResolver 는 ")
class AuthArgumentResolverTest {

    private final AuthenticationContext context = mock(AuthenticationContext.class);
    private final AuthArgumentResolver authArgumentResolver = new AuthArgumentResolver(context);

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("@Auth 가 붙어있다면 지원할 수 있다.")
        void success_test_1() {
            // given
            MethodParameter methodParameter = mock(MethodParameter.class);
            when(methodParameter.hasParameterAnnotation(Auth.class))
                    .thenReturn(true);

            // when
            boolean result = authArgumentResolver.supportsParameter(methodParameter);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("resolveArgument() 시 AuthenticationContext의 principle을 반환한다.")
        void success_test_2() {
            // when
            authArgumentResolver.resolveArgument(
                    mock(MethodParameter.class),
                    mock(ModelAndViewContainer.class),
                    mock(NativeWebRequest.class),
                    mock(WebDataBinderFactory.class)
            );

            // then
            verify(context, times(1)).principal();
        }
    }
}