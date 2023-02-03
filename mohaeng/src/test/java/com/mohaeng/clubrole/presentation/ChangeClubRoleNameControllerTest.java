package com.mohaeng.clubrole.presentation;

import com.mohaeng.clubrole.application.usecase.ChangeClubRoleNameUseCase;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.clubrole.presentation.ChangeClubRoleNameController.ChangeClubRoleNameRequest;
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

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NOT_FOUND_CLUB_ROLE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_ROLE_NAME;
import static com.mohaeng.clubrole.presentation.ChangeClubRoleNameController.CHANGE_CLUB_ROLE_NAME_URL;
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
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChangeClubRoleNameController.class)
@DisplayName("ChangeClubRoleNameController 는 ")
class ChangeClubRoleNameControllerTest extends ControllerTest {

    @MockBean
    private ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    private static final ChangeClubRoleNameRequest request = new ChangeClubRoleNameRequest("변경할 역할 이름");

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임의 회장 혹은 임원진이 역할 이름 변경 요청을 보낸 경우 역할 이름을 변경하고 200을 반환한다.")
        void success_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            doNothing().when(changeClubRoleNameUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_CLUB_ROLE_NAME_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(changeClubRoleNameUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-club-role-name",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("clubRoleId").description("이름을 변경할 모임 역할의 ID")
                            ),
                            requestFields(
                                    fieldWithPath("roleName").type(STRING).description("name(변경할 역할의 이름)")
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
                            post(CHANGE_CLUB_ROLE_NAME_URL, 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(changeClubRoleNameUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("change-club-role-name(No Access Token)",
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
                    .when(changeClubRoleNameUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_CLUB_ROLE_NAME_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(changeClubRoleNameUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-club-role-name(Nonexistent Participant)",
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
            doThrow(new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME))
                    .when(changeClubRoleNameUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_CLUB_ROLE_NAME_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    ).andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(changeClubRoleNameUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-club-role-name(requester does not president or officer)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("모임 역할 이름 변경 요청 시 필드가 없는 경우 400을 반환한다.")
        void fail_test_4() throws Exception {
            CreateClubRoleController.CreateClubRoleRequest emptyRequest = new CreateClubRoleController.CreateClubRoleRequest("", null);
            setAuthentication(1L);
            ChangeClubRoleNameRequest request = new ChangeClubRoleNameRequest(" ");
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_CLUB_ROLE_NAME_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(changeClubRoleNameUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("change-club-role-name(request fields contains empty value)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }

        @Test
        @DisplayName("이름을 변경하려는 역할이 없는 경우 404를 반환한다.")
        void fail_test_5() throws Exception {
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ClubRoleException(NOT_FOUND_CLUB_ROLE))
                    .when(changeClubRoleNameUseCase).command(any());
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_CLUB_ROLE_NAME_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(changeClubRoleNameUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-club-role-name(Nonexistent ClubRole)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }
    }
}