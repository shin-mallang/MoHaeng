package com.mohaeng.common.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohaeng.authentication.application.usecase.CreateTokenUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.domain.model.Claims;
import com.mohaeng.authentication.infrastructure.jwt.service.CreateToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mohaeng.authentication.application.service.LogIn.MEMBER_ID_CLAIM;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequestBuilder {

    private CreateTokenUseCase createTokenUseCase = new CreateToken(new MockJwtProperties());

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String url;
    private List<Object> urlVariables;
    private HttpMethod method;
    private Map<String, Object> headers = new HashMap<>();
    private MediaType contentType;
    private Object content;
    private HttpStatus status;

    private RequestBuilder(final MockMvc mockMvc, final ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = mapper;
    }

    public static Method request(final MockMvc mockMvc, final ObjectMapper objectMapper) {
        return new RequestBuilder(mockMvc, objectMapper)
                .request();
    }

    public static EndPoint getRequest(final MockMvc mockMvc, final ObjectMapper objectMapper) {
        return new RequestBuilder(mockMvc, objectMapper)
                .request()
                .get();
    }

    public static EndPoint postRequest(final MockMvc mockMvc, final ObjectMapper objectMapper) {
        return new RequestBuilder(mockMvc, objectMapper)
                .request()
                .post();
    }

    public static EndPoint deleteRequest(final MockMvc mockMvc, final ObjectMapper objectMapper) {
        return new RequestBuilder(mockMvc, objectMapper)
                .request()
                .delete();
    }

    private Method request() {
        return new Method();
    }

    public class Method {
        public EndPoint method(final HttpMethod methodInput) {
            method = methodInput;
            return new EndPoint();
        }

        public EndPoint get() {
            method = GET;
            return new EndPoint();
        }

        public EndPoint post() {
            method = POST;
            return new EndPoint();
        }

        public EndPoint delete() {
            method = DELETE;
            return new EndPoint();
        }
    }

    public class EndPoint {
        public Authentication url(final String urlInput, final Object... urlVariablesInput) {
            url = urlInput;
            urlVariables = Arrays.asList(urlVariablesInput);
            return new Authentication();
        }
    }

    public class Authentication {

        public Content login() {
            Claims claims = new Claims();
            claims.addClaims(MEMBER_ID_CLAIM, "1");
            AccessToken accessToken = new AccessToken(createTokenUseCase.command(new CreateTokenUseCase.Command(claims)));
            headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.token());
            return new Content();
        }

        public Content login(final long id) {
            Claims claims = new Claims();
            claims.addClaims(MEMBER_ID_CLAIM, String.valueOf(id));
            AccessToken accessToken = new AccessToken(createTokenUseCase.command(new CreateTokenUseCase.Command(claims)));
            headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.token());
            return new Content();
        }

        public Content noLogin() {
            return new Content();
        }
    }

    public class Content {
        public Expect jsonContent(final Object object) {
            contentType = MediaType.APPLICATION_JSON;
            content = object;
            return new Expect();
        }

        public Expect noContent() {
            return new Expect();
        }
    }

    public class Expect {
        public ResultActions expectStatus(HttpStatus expectStatus) throws Exception {
            status = expectStatus;
            return makeRequest();
        }

        public ResultActions ok() throws Exception {
            status = HttpStatus.OK;
            return makeRequest();
        }

        public ResultActions created() throws Exception {
            status = HttpStatus.CREATED;
            return makeRequest();
        }

        public ResultActions unAuthorized() throws Exception {
            status = HttpStatus.UNAUTHORIZED;
            return makeRequest();
        }

        public ResultActions forbidden() throws Exception {
            status = HttpStatus.FORBIDDEN;
            return makeRequest();
        }

        public ResultActions badRequest() throws Exception {
            status = HttpStatus.BAD_REQUEST;
            return makeRequest();
        }

        public ResultActions notFound() throws Exception {
            status = HttpStatus.NOT_FOUND;
            return makeRequest();
        }

        public ResultActions conflict() throws Exception {
            status = HttpStatus.CONFLICT;
            return makeRequest();
        }
    }

    private ResultActions makeRequest() throws Exception {
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders.request(method, url, urlVariables.toArray());
        for (String header : headers.keySet()) {
            request.header(header, headers.get(header));
        }

        if (content != null) {
            request.contentType(contentType)
                    .content(objectMapper.writeValueAsString(content));
        }

        return mockMvc.perform(request)
                .andExpect(status().is(status.value()));
    }
}
