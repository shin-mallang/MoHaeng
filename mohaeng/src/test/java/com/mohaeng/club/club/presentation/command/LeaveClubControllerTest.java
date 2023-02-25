package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.LeaveClubUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.presentation.ControllerTest;
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
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("LeaveClubController(모임 탈퇴 컨트롤러) 는")
@WebMvcTest(LeaveClubController.class)
class LeaveClubControllerTest extends ControllerTest {

    @MockBean
    private LeaveClubUseCase leaveClubUseCase;

    private final Long participantId = 1L;

    @Test
    void 모임_탈퇴_성공_시_200을_반환한다() throws Exception {
        // given
        doNothing().when(leaveClubUseCase).command(any());

        // when
        ResultActions resultActions = deleteRequest()
                .url(LEAVE_CLUB_URL, participantId)
                .login()
                .noContent()
                .ok();

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
        doNothing().when(leaveClubUseCase).command(any());

        // when & then
        ResultActions resultActions = deleteRequest()
                .url(LEAVE_CLUB_URL, participantId)
                .noLogin()
                .noContent()
                .unAuthorized();

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
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(leaveClubUseCase).command(any());

        // when & then
        ResultActions resultActions = deleteRequest()
                .url(LEAVE_CLUB_URL, participantId)
                .login()
                .noContent()
                .notFound();

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
        doThrow(new ParticipantException(PRESIDENT_CAN_NOT_LEAVE_CLUB))
                .when(leaveClubUseCase).command(any());

        // when & then
        ResultActions resultActions = deleteRequest()
                .url(LEAVE_CLUB_URL, participantId)
                .login()
                .noContent()
                .badRequest();

        verify(leaveClubUseCase, times(1)).command(any());

        resultActions.andDo(
                document("club/participant/leave-club/fail/president requests to leave the club",
                        getDocumentResponse()
                )
        );
    }
}