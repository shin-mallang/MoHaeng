package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.DeleteClubUseCase;
import com.mohaeng.club.club.exception.ClubException;
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
import static com.mohaeng.club.club.exception.ClubExceptionType.NO_AUTHORITY_DELETE_CLUB;
import static com.mohaeng.club.club.presentation.command.DeleteClubController.DELETE_CLUB_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("DeleteClubController(참여자 제거 컨트롤러) 는")
@WebMvcTest(DeleteClubController.class)
class DeleteClubControllerTest extends ControllerTest {

    @MockBean
    private DeleteClubUseCase deleteClubUseCase;

    @Test
    void 모임의_회장이_모임_제거_요청을_보낸_경우_모임을_제거하고_200을_반환한다() throws Exception {
        // when & then
        ResultActions resultActions = deleteRequest()
                .url(DELETE_CLUB_URL, 1L)
                .login()
                .expect()
                .ok();

        then(deleteClubUseCase).should().command(any());
        resultActions.andDo(
                document("club/club/delete-club",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("제거할 모임의 ID")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // when & then
        ResultActions resultActions = deleteRequest()
                .url(DELETE_CLUB_URL, 1L)
                .noLogin()
                .expect()
                .unAuthorized();

        then(deleteClubUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/club/delete-club/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임_ID_가_없는_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(deleteClubUseCase).command(any());

        // when & then
        ResultActions resultActions = deleteRequest()
                .url(DELETE_CLUB_URL, 1L)
                .login()
                .expect()
                .notFound();

        then(deleteClubUseCase).should().command(any());
        resultActions.andDo(
                document("club/club/delete-club/fail/Nonexistent Club ID",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_회장이_아닌_경우_403을_반환한다() throws Exception {
        // given
        doThrow(new ClubException(NO_AUTHORITY_DELETE_CLUB))
                .when(deleteClubUseCase).command(any());

        // when & then
        ResultActions resultActions = deleteRequest()
                .url(DELETE_CLUB_URL, 1L)
                .login()
                .expect()
                .forbidden();

        then(deleteClubUseCase).should().command(any());
        resultActions.andDo(
                document("club/club/delete-club/fail/requester does not president",
                        getDocumentResponse()
                )
        );
    }
}