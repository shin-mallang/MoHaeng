

package com.mohaeng.presentation.api.authentication;

import com.mohaeng.application.authentication.exception.IncorrectAuthenticationException;
import com.mohaeng.application.authentication.usecase.LogInUseCase;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.presentation.api.authentication.LogInController.LOGIN_URL;
import static com.mohaeng.util.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogInController.class)
@DisplayName("LogInController 는 ")
class LogInControllerTest extends ControllerTest {

    @MockBean
    private LogInUseCase logInUseCase;

    private final LogInController.LoginRequest loginRequest = new LogInController.LoginRequest("sampleUsername", "samplePassword");
    private final LogInController.LoginRequest emptyLoginRequest = new LogInController.LoginRequest("", "");
    private final String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Test
    @DisplayName("로그인 성공 시 200과 AccessToken을 반환한다.")
    void loginSuccessWillReturn200AndAccessToken() throws Exception {
        when(logInUseCase.command(any()))
                .thenReturn(new AccessToken(jwt));

        ResultActions resultActions = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isOk());

        resultActions.andDo(
                document("login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("username").type(STRING).description("username(아이디)"),
                                fieldWithPath("password").type(STRING).description("password(비밀번호)")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 실패 시 401 예외를 반환한다.")
    void loginFailWillReturn401() throws Exception {
        when(logInUseCase.command(any()))
                .thenThrow(new IncorrectAuthenticationException());

        ResultActions resultActions = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        resultActions.andDo(
                document("login fail(username or password miss match)",
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("요청 필드가 없는 경우 400 예외를 반환한다.")
    void loginFailCauseByEmptyRequestFieldWillReturn400() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(emptyLoginRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        resultActions.andDo(
                document("login fail(request fields contains empty value)",
                        getDocumentResponse()
                ));
    }
}