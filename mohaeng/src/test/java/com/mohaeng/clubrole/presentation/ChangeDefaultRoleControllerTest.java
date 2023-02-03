package com.mohaeng.clubrole.presentation;

import com.mohaeng.clubrole.application.usecase.ChangeDefaultRoleUseCase;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.ControllerTest;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.*;
import static com.mohaeng.clubrole.presentation.ChangeDefaultRoleController.CHANGE_DEFAULT_ROLE_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChangeDefaultRoleController.class)
@DisplayName("ChangeDefaultRoleController 는 ")
class ChangeDefaultRoleControllerTest extends ControllerTest {

    @MockBean
    private ChangeDefaultRoleUseCase changeDefaultRoleUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임의 회장 혹은 임원진이 기본 역할 변경 요청을 보낸 경우 기본 역할을 변경하고 200을 반환한다.")
        void success_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            doNothing().when(changeDefaultRoleUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_DEFAULT_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(changeDefaultRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-default-club-role",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("clubRoleId").description("기본 역할로 설정할 모임 역할의 ID")
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
        void fail_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_DEFAULT_ROLE_URL, 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(changeDefaultRoleUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("change-default-club-role(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("해당 회원이 역할을 변경하려는 모임에 가입되어있지 않은 경우 404를 반환한다.")
        void fail_test_2() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                    .when(changeDefaultRoleUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_DEFAULT_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(changeDefaultRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-default-club-role(Nonexistent Participant)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("요청자가 회장 혹은 임원이 아닌 경우 403를 반환한다.")
        void fail_test_3() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ClubRoleException(NO_AUTHORITY_CHANGE_DEFAULT_ROLE))
                    .when(changeDefaultRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_DEFAULT_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(changeDefaultRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-default-club-role(requester does not president or officer)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("기본 역할을 변경하려는 역할이 존재하지 않는 경우 404를 반환한다.")
        void fail_test_4() throws Exception {
            doThrow(new ClubRoleException(NOT_FOUND_CLUB_ROLE))
                    .when(changeDefaultRoleUseCase).command(any());
            setAuthentication(1L);
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_DEFAULT_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(changeDefaultRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-default-club-role(Nonexistent ClubRole)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }

        @Test
        @DisplayName("이미 기본 역할인 경우 400을 반환한다.")
        void fail_test_5() throws Exception {
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ClubRoleException(ALREADY_DEFAULT_ROLE))
                    .when(changeDefaultRoleUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_DEFAULT_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(changeDefaultRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-default-club-role(already default role)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }
    }
}