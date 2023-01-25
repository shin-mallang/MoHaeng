package com.mohaeng.participant.presentation;

import com.mohaeng.common.ControllerTest;
import com.mohaeng.participant.application.usecase.ExpelParticipantUseCase;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static com.mohaeng.participant.exception.ParticipantExceptionType.*;
import static com.mohaeng.participant.presentation.ExpelParticipantController.EXPEL_PARTICIPANT_URL;
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

@WebMvcTest(ExpelParticipantController.class)
class ExpelParticipantControllerTest extends ControllerTest {

    @MockBean
    private ExpelParticipantUseCase expelParticipantUseCase;

    @Test
    @DisplayName("모임에서 추방 성공")
    void success_test_1() throws Exception {
        // given
        final Long participantId = 1L;
        final Long memberId = 1L;

        doNothing().when(expelParticipantUseCase).command(any());
        setAuthentication(memberId);

        // when
        ResultActions resultActions = mockMvc.perform(
                        delete(EXPEL_PARTICIPANT_URL, participantId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                ).andDo(print())
                .andExpect(status().isOk());

        // then
        verify(expelParticipantUseCase, times(1)).command(any());

        resultActions.andDo(
                document("expel-participant-from-club",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("participantId").description("추방시킬 참여자(Participant) ID")
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
            doNothing().when(expelParticipantUseCase).command(any());
            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(EXPEL_PARTICIPANT_URL, participantId)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            // when & then
            verify(expelParticipantUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("expel-participant-from-club(No Access Token)",
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
                    .when(expelParticipantUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(EXPEL_PARTICIPANT_URL, participantId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isNotFound());

            // when & then
            verify(expelParticipantUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("expel-participant-from-club(Nonexistent Participant ID)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("요청자가 회장이 아닌 경우 403")
        void fail_test_3() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(NO_AUTHORITY_EXPEL_PARTICIPANT))
                    .when(expelParticipantUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(EXPEL_PARTICIPANT_URL, participantId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(expelParticipantUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("expel-participant-from-club(requester does not president)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("모임에 회장이 없는 경우 404 (발생하지 않음)")
        void fail_test_4() throws Exception {
            // given
            final Long participantId = 1L;
            final Long memberId = 1L;
            doThrow(new ParticipantException(NOT_FOUND_PRESIDENT))
                    .when(expelParticipantUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(EXPEL_PARTICIPANT_URL, participantId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isNotFound());

            // when & then
            verify(expelParticipantUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("expel-participant-from-club(Nonexistent president in club)",
                            getDocumentResponse()
                    )
            );
        }
    }
}