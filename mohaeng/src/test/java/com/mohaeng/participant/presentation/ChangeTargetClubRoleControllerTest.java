package com.mohaeng.participant.presentation;

import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.ControllerTest;
import com.mohaeng.participant.application.usecase.ChangeTargetClubRoleUseCase;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NOT_FOUND_CLUB_ROLE;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static com.mohaeng.participant.exception.ParticipantExceptionType.*;
import static com.mohaeng.participant.presentation.ChangeTargetClubRoleController.CHANGE_TARGET_CLUB_ROLE_URL;
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

@WebMvcTest(ChangeTargetClubRoleController.class)
@DisplayName("ChangeTargetClubRoleController 는 ")
class ChangeTargetClubRoleControllerTest extends ControllerTest {

    @MockBean
    private ChangeTargetClubRoleUseCase changeTargetClubRoleUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {
        @Test
        @DisplayName("대상의 역할 변경 성공")
        void success_test_1() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;

            doNothing().when(changeTargetClubRoleUseCase).command(any());
            setAuthentication(memberId);

            // when
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isOk());

            // then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("participantId").description("역할을 변경할 참여자의 ID"),
                                    parameterWithName("clubRoleId").description("변경하고싶은 역할의 ID")
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("인증되지 않은 사용자의 경우 401")
        void fail_test_1() throws Exception {
            // given
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;

            doNothing().when(changeTargetClubRoleUseCase).command(any());
            setAuthentication(memberId);

            // when
            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                    ).andDo(print())
                    .andExpect(status().isUnauthorized());

            // when & then
            verify(changeTargetClubRoleUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("역할을 변경할 대상 참가자의 ID가 없는 경우 404")
        void fail_test_2() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                    .when(changeTargetClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isNotFound());

            // when & then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(Nonexistent Participant ID)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("변경하고자 하는 역할의 ID가 없는 경우 404")
        void fail_test_3() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;
            doThrow(new ClubRoleException(NOT_FOUND_CLUB_ROLE))
                    .when(changeTargetClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isNotFound());

            // when & then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(Nonexistent ClubRole ID)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("일반 회원이 변경을 요청한 경우 403")
        void fail_test_4() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(NO_AUTHORITY_CHANGE_TARGET_ROLE))
                    .when(changeTargetClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(requester is general participant)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("바꾸려는 역할이 다른 모임의 역할인 경우 400")
        void fail_test_5() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(CAN_NOT_CHANGE_TO_OTHER_CLUB_ROLE))
                    .when(changeTargetClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(change other club role)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("바꾸려는 회원이 다른 모임의 회원인 경우 404 (참여자를 찾을 수 없다는 예외)")
        void fail_test_6() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                    .when(changeTargetClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andDo(print())
                    .andExpect(status().isNotFound());

            // when & then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(target is other club)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("회장으로 변경하려는 경우 400")
        void fail_test_7() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(CAN_NOT_CHANGED_TO_PRESIDENT_ROLE))
                    .when(changeTargetClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(change to president role)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("자신과 계급이 동일하거나, 자신보다 높은 계급의 회원의 역할을 변경하려는 경우 403")
        void fail_test_8() throws Exception {
            // given
            final Long participantId = 1L;
            final Long clubRoleId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(NO_AUTHORITY_CHANGE_TARGET_ROLE))
                    .when(changeTargetClubRoleUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            post(CHANGE_TARGET_CLUB_ROLE_URL, participantId, clubRoleId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(changeTargetClubRoleUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("change-participant's-role(target is same rank or higher rank than requester)",
                            getDocumentResponse()
                    )
            );
        }
    }
}