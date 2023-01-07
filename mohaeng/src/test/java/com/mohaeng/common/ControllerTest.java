package com.mohaeng.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohaeng.authentication.application.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.authentication.application.usecase.ExtractClaimsUseCase;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.authentication.infrastructure.jwt.service.ExtractAccessToken;
import com.mohaeng.authentication.presentation.argumentresolver.AuthArgumentResolver;
import com.mohaeng.authentication.presentation.interceptor.AuthenticationContext;
import com.mohaeng.authentication.presentation.interceptor.LogInInterceptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static com.mohaeng.authentication.application.service.LogIn.MEMBER_ID_CLAIM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 이를 상속받는 클래스에서는 반드시 @WebMvcTest 를 달아주어야 함
 */
@Import({MockMvcConfig.class, ExtractAccessToken.class})
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AuthArgumentResolver authArgumentResolver;

    @Autowired
    protected LogInInterceptor logInInterceptor;

    @Autowired
    protected ExtractAccessTokenUseCase extractAccessTokenUseCase;

    @MockBean
    protected ExtractClaimsUseCase extractClaimsUseCase;

    @MockBean
    protected AuthenticationContext authenticationContext;

    /**
     * 인증된 회원이 필요한 경우 헤당 메서드를 사용하고, ACCESS TOKEN을 HEADER에 세팅한다.
     */
    protected void setAuthentication(final Long id) {
        Claims claims = new Claims(new HashMap<>() {{
            put(MEMBER_ID_CLAIM, String.valueOf(id));
        }});
        when(extractClaimsUseCase.command(any()))
                .thenReturn(claims);
        when(authenticationContext.principal())
                .thenReturn(id);
    }
}