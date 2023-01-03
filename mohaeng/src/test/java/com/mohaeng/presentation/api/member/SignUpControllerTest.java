package com.mohaeng.presentation.api.member;

import com.mohaeng.application.member.exception.DuplicateUsernameException;
import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.domain.member.domain.enums.Gender;
import com.mohaeng.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.util.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignUpController.class)
@DisplayName("SignUpController는 ")
class SignUpControllerTest extends ControllerTest {

    @MockBean
    private SignUpUseCase signUpUseCase;

    private final SignUpController.SignUpRequest signUpRequest =
            new SignUpController.SignUpRequest("username", "password", "name", 22, Gender.MAN);

    private final SignUpController.SignUpRequest nullRequest =
            new SignUpController.SignUpRequest("", "", "", 0, Gender.MAN);

    @Test
    @DisplayName("회원가입(signUp) 성공 시 201을 반환한다.")
    void signUpSuccessWillReturn201() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        post("/api/v1/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated());

        resultActions.andDo(
                document("sign-up",
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

    @Test
    @DisplayName("회원가입(signUp)시 중복 아이디인 경우 409를 반환한다.")
    void signUpFailCauseByDuplicatedUsernameWillReturn409() throws Exception {

        doThrow(new DuplicateUsernameException()).when(signUpUseCase).command(any());

        ResultActions resultActions = mockMvc.perform(
                        post("/api/v1/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andDo(print())
                .andExpect(status().isConflict());

        resultActions.andDo(
                document("sign-up fail(duplicated username)",
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("회원가입(signUp)시 필드가 없는 경우 400을 반환한다.")
    void signUpFailCauseByEmptyRequestFieldWillReturn400() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        post("/api/v1/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nullRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        resultActions.andDo(
                document("sign-up fail(request fields contains empty value)",
                        getDocumentResponse()
                ));
    }
}