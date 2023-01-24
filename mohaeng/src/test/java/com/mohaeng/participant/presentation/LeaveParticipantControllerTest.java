package com.mohaeng.participant.presentation;

import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.ControllerTest;
import com.mohaeng.participant.application.usecase.LeaveParticipantUseCase;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_EMPTY;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static com.mohaeng.participant.exception.ParticipantExceptionType.*;
import static com.mohaeng.participant.presentation.LeaveParticipantController.LEAVE_PARTICIPANT_URL;
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

@WebMvcTest(LeaveParticipantController.class)
class LeaveParticipantControllerTest extends ControllerTest {

    @MockBean
    private LeaveParticipantUseCase leaveParticipantUseCase;

    @Test
    @DisplayName("모임 탈퇴 성공")
    void success_test_1() throws Exception {
        // given
        final Long participantId = 1L;
        final Long memberId = 1L;

        doNothing().when(leaveParticipantUseCase).command(any());
        setAuthentication(memberId);

        // when
        ResultActions resultActions = mockMvc.perform(
                        delete(LEAVE_PARTICIPANT_URL, participantId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                ).andDo(print())
                .andExpect(status().isOk());

        // then
        verify(leaveParticipantUseCase, times(1)).command(any());

        resultActions.andDo(
                document("leave-participant-from-club",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("participantId").description("참여자(Participant) ID")
                        )
                )
        );
    }

    @Nested
    @DisplayName("모임 탈퇴 실패 테스트")
    class FailTest {
        /**
         * 남은 사람이 1명이라 탈퇴가 불가능한 경우
         */

        @Test
        @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
        void fail_test_1() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            doNothing().when(leaveParticipantUseCase).command(any());
            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(LEAVE_PARTICIPANT_URL, participantId)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            // when & then
            verify(leaveParticipantUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("leave-participant-from-club(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("참가자 ID가 없는 경우 404 를 반환한다.")
        void fail_test_2() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                    .when(leaveParticipantUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(LEAVE_PARTICIPANT_URL, participantId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isNotFound());

            // when & then
            verify(leaveParticipantUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("leave-participant-from-club(Nonexistent Participant ID)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("요청한 회원의 id가 참가자의 MemberId와 일치하지 않는 경우 403")
        void fail_test_3() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(MISMATCH_BETWEEN_PARTICIPANT_AND_MEMBER))
                    .when(leaveParticipantUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(LEAVE_PARTICIPANT_URL, participantId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(leaveParticipantUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("leave-participant-from-club(requested memberId does not match the participant's MemberId)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("회장이 모임 탈퇴 요청을 한 경우 400")
        void fail_test_4() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(PRESIDENT_CAN_NOT_LEAVE_CLUB))
                    .when(leaveParticipantUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(LEAVE_PARTICIPANT_URL, participantId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(leaveParticipantUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("leave-participant-from-club(president requests to leave the club)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("남은 사람이 1명이라 탈퇴가 불가능한 경우 (회장은 탈퇴할 수 없으며, 먼저 검사되므로 실제로 호출될 일은 없다.)")
        void fail_test_5() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            doThrow(new ClubException(CLUB_IS_EMPTY))
                    .when(leaveParticipantUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(LEAVE_PARTICIPANT_URL, participantId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(leaveParticipantUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("leave-participant-from-club(club has only one participant)",
                            getDocumentResponse()
                    )
            );
        }
    }
}