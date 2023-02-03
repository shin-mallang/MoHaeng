package com.mohaeng.clubrole.presentation;

import com.mohaeng.clubrole.application.usecase.CreateClubRoleUseCase;
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

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NO_AUTHORITY_CREATE_ROLE;
import static com.mohaeng.clubrole.presentation.CreateClubRoleController.CREATE_CLUB_ROLE_URL;
import static com.mohaeng.clubrole.presentation.CreateClubRoleController.CreateClubRoleRequest;
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

@WebMvcTest(CreateClubRoleController.class)
@DisplayName("CreateClubRoleController 는 ")
class CreateClubRoleControllerTest extends ControllerTest {

    @MockBean
    private CreateClubRoleUseCase createClubRoleUseCase;

    private static final CreateClubRoleRequest request = new CreateClubRoleRequest("역할 이름!", OFFICER);

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임의 회장 혹은 임원진이 역할 생성 요청을 보낸 경우 역할을 생성하고 201을 반환한다.")
        void success_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            when(createClubRoleUseCase.command(any())).thenReturn(1L);

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isCreated());

            verify(createClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("create-club-role",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("clubId").description("역할을 생성할 모임의 ID")
                            ),
                            requestFields(
                                    fieldWithPath("name").type(STRING).description("name(생성할 역할의 이름)"),
                                    fieldWithPath("category").type(STRING).description("category(생성할 역할의 카테고리[OFFICER, GENERAL])")
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
                            post(CREATE_CLUB_ROLE_URL, 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(createClubRoleUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("create-club-role(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("해당 회원이 역할을 생성하려는 모임에 가입되어있지 않은 경우 404를 반환한다.")
        void fail_test_2() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                    .when(createClubRoleUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(createClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("create-club-role(Nonexistent Participant)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("요청자가 회장 혹은 임원이 아닌 경우 403를 반환한다.")
        void fail_test_3() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ClubRoleException(NO_AUTHORITY_CREATE_ROLE))
                    .when(createClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    ).andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(createClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("create-club-role(requester does not president or officer)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("회장 역할을 생성하려는 경우 400을 반환한다.")
        void fail_test_4() throws Exception {
            // given
            CreateClubRoleRequest request = new CreateClubRoleRequest("역할 이름!", PRESIDENT);
            final Long participantId = 1L;
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ClubRoleException(CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE))
                    .when(createClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    ).andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(createClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("create-club-role(when create president role)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("모임 역할 생성 시 필드가 없는 경우 400을 반환한다.")
        void fail_test_5() throws Exception {
            CreateClubRoleRequest emptyRequest = new CreateClubRoleRequest("", null);
            setAuthentication(1L);
            CreateClubRoleRequest request = new CreateClubRoleRequest("역할 이름!", OFFICER);
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(emptyRequest))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(createClubRoleUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("create-club-role(request fields contains empty value)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }

        @Test
        @DisplayName("모임 역할 생성 시 카테고리 필드가 잘못된 경우 400을 반환한다.")
        void fail_test_6() throws Exception {
            setAuthentication(1L);
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"name\": \"name\",\"category\":  \"cateGory\"}")
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(createClubRoleUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("create-club-role(category enum mapping fail)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }
    }
}