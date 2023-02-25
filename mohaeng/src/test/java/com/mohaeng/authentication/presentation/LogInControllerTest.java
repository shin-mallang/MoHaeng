

package com.mohaeng.authentication.presentation;

import com.mohaeng.authentication.application.usecase.LogInUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.exception.AuthenticationException;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.INCORRECT_AUTHENTICATION;
import static com.mohaeng.authentication.presentation.LogInController.LOGIN_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@WebMvcTest(controllers = LogInController.class)
@DisplayName("LogInController(로그인 컨트롤러) 는")
class LogInControllerTest extends ControllerTest {

    @MockBean
    private LogInUseCase logInUseCase;

    private final LogInController.LoginRequest loginRequest = new LogInController.LoginRequest("sampleUsername", "samplePassword");
    private final LogInController.LoginRequest emptyLoginRequest = new LogInController.LoginRequest("", "");
    private final String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("로그인 성공 시 200과 AccessToken을 반환한다.")
        void success_test_1() throws Exception {
            when(logInUseCase.command(any()))
                    .thenReturn(new AccessToken(jwt));

            ResultActions resultActions = postRequest()
                    .url(LOGIN_URL)
                    .noLogin()
                    .jsonContent(loginRequest)
                    .ok();

            resultActions.andDo(
                    document("authentication/login",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("username").type(STRING).description("username(아이디)"),
                                    fieldWithPath("password").type(STRING).description("password(비밀번호)")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("로그인 실패 시 401 예외를 반환한다.")
        void fail_test_1() throws Exception {
            when(logInUseCase.command(any()))
                    .thenThrow(new AuthenticationException(INCORRECT_AUTHENTICATION));

            ResultActions resultActions = postRequest()
                    .url(LOGIN_URL)
                    .noLogin()
                    .jsonContent(loginRequest)
                    .unAuthorized();

            resultActions.andDo(
                    document("authentication/login/fail/username or password miss match",
                            getDocumentResponse()
                    ));
        }

        @Test
        @DisplayName("요청 필드가 없는 경우 400 예외를 반환한다.")
        void fail_test_2() throws Exception {
            ResultActions resultActions = postRequest()
                    .url(LOGIN_URL)
                    .noLogin()
                    .jsonContent(emptyLoginRequest)
                    .badRequest();

            resultActions.andDo(
                    document("authentication/login/fail/request fields contains empty value",
                            getDocumentResponse()
                    ));
        }
    }
}