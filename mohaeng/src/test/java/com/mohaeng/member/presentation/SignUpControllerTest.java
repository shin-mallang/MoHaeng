package com.mohaeng.member.presentation;

import com.mohaeng.common.presentation.ControllerTest;
import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.enums.Gender;
import com.mohaeng.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.common.fixtures.MemberFixture.signUpRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.member.exception.MemberExceptionType.DUPLICATE_USERNAME;
import static com.mohaeng.member.presentation.SignUpController.SIGN_UP_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@WebMvcTest(controllers = SignUpController.class)
@DisplayName("SignUpController(회원가입 컨트롤러) 는 ")
class SignUpControllerTest extends ControllerTest {

    @MockBean
    private SignUpUseCase signUpUseCase;

    private final SignUpController.SignUpRequest signUpRequest = signUpRequest("username", "password", "name", 22, Gender.MAN);

    private final SignUpController.SignUpRequest nullRequest = signUpRequest("", "", "", 0, Gender.MAN);

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("회원가입(signUp) 성공 시 201을 반환한다.")
        void success_test_1() throws Exception {
            ResultActions resultActions = postRequest()
                    .url(SIGN_UP_URL)
                    .noLogin()
                    .jsonContent(signUpRequest)
                    .created();

            resultActions.andDo(
                    document("member/sign-up",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("username").type(STRING).description("username(아이디)"),
                                    fieldWithPath("password").type(STRING).description("password(비밀번호)"),
                                    fieldWithPath("name").type(STRING).description("name(이름)"),
                                    fieldWithPath("age").type(NUMBER).description("age(나이)"),
                                    fieldWithPath("gender").type(STRING).description("gender(성별)")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("회원가입(signUp)시 중복 아이디인 경우 409를 반환한다.")
        void fail_test_1() throws Exception {
            // given
            doThrow(new MemberException(DUPLICATE_USERNAME)).when(signUpUseCase).command(any());

            // when & then
            ResultActions resultActions = postRequest()
                    .url(SIGN_UP_URL)
                    .noLogin()
                    .jsonContent(signUpRequest)
                    .conflict();

            resultActions.andDo(
                    document("member/sign-up/fail/duplicated username",
                            getDocumentResponse()
                    ));
        }

        @Test
        @DisplayName("회원가입(signUp)시 필드가 없는 경우 400을 반환한다.")
        void fail_test_2() throws Exception {
            // when & then
            ResultActions resultActions = postRequest()
                    .url(SIGN_UP_URL)
                    .noLogin()
                    .jsonContent(nullRequest)
                    .badRequest();

            resultActions.andDo(
                    document("member/sign-up/fail/request fields contains empty value",
                            getDocumentResponse()
                    ));
        }
    }
}