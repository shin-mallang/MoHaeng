package com.mohaeng.common.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohaeng.authentication.application.usecase.ExtractAccessTokenUseCase;
import com.mohaeng.authentication.application.usecase.ExtractClaimsUseCase;
import com.mohaeng.authentication.infrastructure.jwt.service.ExtractAccessToken;
import com.mohaeng.authentication.infrastructure.jwt.service.ExtractClaims;
import com.mohaeng.authentication.presentation.argumentresolver.AuthArgumentResolver;
import com.mohaeng.authentication.presentation.interceptor.AuthInterceptor;
import com.mohaeng.authentication.presentation.interceptor.AuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 이를 상속받는 클래스에서는 반드시 @WebMvcTest 를 달아주어야 함
 */
@Import({MockMvcConfig.class, ExtractAccessToken.class, ExtractClaims.class, AuthenticationContext.class, MockJwtProperties.class})
@AutoConfigureRestDocs
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AuthArgumentResolver authArgumentResolver;

    @Autowired
    protected AuthInterceptor authInterceptor;

    @Autowired
    protected ExtractAccessTokenUseCase extractAccessTokenUseCase;

    @Autowired
    protected ExtractClaimsUseCase extractClaimsUseCase;

    @Autowired
    protected AuthenticationContext authenticationContext;

    protected RequestBuilder.Method request() {
        return RequestBuilder.request(mockMvc, objectMapper);
    }

    protected RequestBuilder.EndPoint getRequest() {
        return RequestBuilder.getRequest(mockMvc, objectMapper);
    }

    protected RequestBuilder.EndPoint postRequest() {
        return RequestBuilder.postRequest(mockMvc, objectMapper);
    }

    protected RequestBuilder.EndPoint deleteRequest() {
        return RequestBuilder.deleteRequest(mockMvc, objectMapper);
    }
}