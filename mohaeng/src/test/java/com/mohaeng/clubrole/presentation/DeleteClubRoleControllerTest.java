package com.mohaeng.clubrole.presentation;

import com.mohaeng.clubrole.application.usecase.DeleteClubRoleUseCase;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.ControllerTest;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.*;
import static com.mohaeng.clubrole.presentation.DeleteClubRoleController.DELETE_CLUB_ROLE_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeleteClubRoleController.class)
@DisplayName("DeleteClubRoleController 는 ")
class DeleteClubRoleControllerTest extends ControllerTest {

    @MockBean
    private DeleteClubRoleUseCase deleteClubRoleUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임의 회장 혹은 임원진이 역할 제거 요청을 보낸 경우 역할을 제거하고 200을 반환한다.")
        void success_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);

            doNothing().when(deleteClubRoleUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(deleteClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club-role",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("clubRoleId").description("제거될 역할 ID")
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
                            delete(DELETE_CLUB_ROLE_URL, 1L)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(deleteClubRoleUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("delete-club-role(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("해당 회원이 역할을 제거하려는 모임에 가입되어있지 않은 경우 404를 반환한다.")
        void fail_test_2() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                    .when(deleteClubRoleUseCase).command(any());

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(deleteClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club-role(Nonexistent Participant)",
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
            doThrow(new ClubRoleException(NO_AUTHORITY_DELETE_ROLE))
                    .when(deleteClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(deleteClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club-role(requester does not president or officer)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("회장 역할을 제거하려는 경우 400을 반환한다.")
        void fail_test_4() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            doThrow(new ClubRoleException(CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE))
                    .when(deleteClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(deleteClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club-role(when delete president role)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("모임 역할 제거 시, 해당 범주에 속하는 역할이 단 한개 뿐이어서 제거할 수 없는 경우 400을 반환한다.")
        void fail_test_5() throws Exception {
            setAuthentication(1L);
            doThrow(new ClubRoleException(CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE))
                    .when(deleteClubRoleUseCase).command(any());
            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(deleteClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club-role(only one role belonging to that category, so cannot be removed)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }

        @Test
        @DisplayName("제거하려는 역할이 없는 경우 404를 반환한다.")
        void fail_test_6() throws Exception {
            setAuthentication(1L);
            doThrow(new ClubRoleException(NOT_FOUND_CLUB_ROLE))
                    .when(deleteClubRoleUseCase).command(any());
            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_ROLE_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(deleteClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club-role(Nonexistent ClubRole)",
                            getDocumentRequest(),
                            getDocumentResponse()
                    ));
        }
    }
}