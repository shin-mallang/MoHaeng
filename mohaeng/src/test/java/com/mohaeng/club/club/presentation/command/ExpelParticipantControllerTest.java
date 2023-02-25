package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.ExpelParticipantUseCase;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT;
import static com.mohaeng.club.club.presentation.command.ExpelParticipantController.EXPEL_PARTICIPANT_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
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

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ExpelParticipantController(회원 추방 컨트롤러) 는")
@WebMvcTest(ExpelParticipantController.class)
class ExpelParticipantControllerTest extends ControllerTest {

    @MockBean
    private ExpelParticipantUseCase expelParticipantUseCase;

    private final Long participantId = 1L;
    private final Long memberId = 1L;
    private final Long clubId = 1L;

    @Test
    void 모임에서_추방_성공_시_200을_반환한다() throws Exception {
        // given
        doNothing().when(expelParticipantUseCase).command(any());
        setAuthentication(memberId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete(EXPEL_PARTICIPANT_URL, clubId, participantId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isOk());

        // then
        verify(expelParticipantUseCase, times(1)).command(any());

        resultActions.andDo(
                document("club/participant/expel-participant-from-club",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("추방시킬 참여자가 존재하는 모임"),
                                parameterWithName("participantId").description("추방시킬 참여자(Participant) ID")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // given
        doNothing().when(expelParticipantUseCase).command(any());
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        delete(EXPEL_PARTICIPANT_URL, clubId, participantId)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // when & then
        verify(expelParticipantUseCase, times(0)).command(any());

        resultActions.andDo(
                document("club/participant/expel-participant-from-club/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 참가자를_찾을수_없는_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(expelParticipantUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                delete(EXPEL_PARTICIPANT_URL, clubId, participantId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isNotFound());

        // when & then
        verify(expelParticipantUseCase, times(1)).command(any());

        resultActions.andDo(
                document("club/participant/expel-participant-from-club/fail/Nonexistent Participant",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_회장이_아닌_경우_403을_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NO_AUTHORITY_EXPEL_PARTICIPANT))
                .when(expelParticipantUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        delete(EXPEL_PARTICIPANT_URL, clubId, participantId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                ).andDo(print())
                .andExpect(status().isForbidden());

        // when & then
        verify(expelParticipantUseCase, times(1)).command(any());

        resultActions.andDo(
                document("club/participant/expel-participant-from-club/fail/requester does not president",
                        getDocumentResponse()
                )
        );
    }
}
