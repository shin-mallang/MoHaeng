package com.mohaeng.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohaeng.application.authentication.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.application.authentication.usecase.ExtractClaimsUseCase;
import com.mohaeng.domain.authentication.model.Claims;
import com.mohaeng.infrastructure.authentication.jwt.service.ExtractAccessToken;
import com.mohaeng.presentation.api.authentication.argumentresolver.AuthArgumentResolver;
import com.mohaeng.presentation.api.authentication.interceptor.AuthenticationContext;
import com.mohaeng.presentation.api.authentication.interceptor.LogInInterceptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static com.mohaeng.application.authentication.service.LogIn.MEMBER_ID_CLAIM;
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