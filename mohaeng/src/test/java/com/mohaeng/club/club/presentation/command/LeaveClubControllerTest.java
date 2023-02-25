package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.LeaveClubUseCase;
import com.mohaeng.club.club.exception.ClubException;
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

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB;
import static com.mohaeng.club.club.presentation.command.LeaveClubController.LEAVE_CLUB_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
@DisplayName("LeaveClubController(모임 탈퇴 컨트롤러) 는")
@WebMvcTest(LeaveClubController.class)
class LeaveClubControllerTest extends ControllerTest {

    @MockBean
    private LeaveClubUseCase leaveClubUseCase;

    @Test
    void 모임_탈퇴_성공_시_200을_반환한다() throws Exception {
        // given
        final Long participantId = 1L;
        final Long memberId = 1L;

        doNothing().when(leaveClubUseCase).command(any());
        setAuthentication(memberId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete(LEAVE_CLUB_URL, participantId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isOk());

        // then
        then(leaveClubUseCase).should().command(any());

        resultActions.andDo(
                document("club/participant/leave-club",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("탈퇴하려는 모임 Id")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // given
        final Long participantId = 1L;
        final Long memberId = 1L;
        doNothing().when(leaveClubUseCase).command(any());
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        delete(LEAVE_CLUB_URL, participantId)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // when & then
        then(leaveClubUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/participant/leave-club/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임_ID가_없는_경우_404_를_반환한다() throws Exception {
        // given
        final Long participantId = 1L;
        final Long memberId = 1L;
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(leaveClubUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        delete(LEAVE_CLUB_URL, participantId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                ).andDo(print())
                .andExpect(status().isNotFound());

        // when & then
        then(leaveClubUseCase).should().command(any());

        resultActions.andDo(
                document("club/participant/leave-club/fail/Nonexistent Club ID",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 회장이_모임_탈퇴_요청을_한_경우_400_을_반환한다() throws Exception {
        // given
        final Long participantId = 1L;
        final Long memberId = 1L;
        doThrow(new ParticipantException(PRESIDENT_CAN_NOT_LEAVE_CLUB))
                .when(leaveClubUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        delete(LEAVE_CLUB_URL, participantId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                ).andDo(print())
                .andExpect(status().isBadRequest());

        // when & then
        verify(leaveClubUseCase, times(1)).command(any());

        resultActions.andDo(
                document("club/participant/leave-club/fail/president requests to leave the club",
                        getDocumentResponse()
                )
        );
    }
}