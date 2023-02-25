package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.DeleteClubUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubExceptionType.NO_AUTHORITY_DELETE_CLUB;
import static com.mohaeng.club.club.presentation.command.DeleteClubController.DELETE_CLUB_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("DeleteClubController(참여자 제거 컨트롤러) 는")
@WebMvcTest(DeleteClubController.class)
class DeleteClubControllerTest extends ControllerTest {

    @MockBean
    private DeleteClubUseCase deleteClubUseCase;

    @Test
    void 모임의_회장이_모임_제거_요청을_보낸_경우_모임을_제거하고_200을_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                delete(DELETE_CLUB_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isOk());

        BDDMockito.then(deleteClubUseCase).should().command(any());
        resultActions.andDo(
                document("delete-club",
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
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        delete(DELETE_CLUB_URL, 1L)
                )
                .andExpect(status().isUnauthorized());

        BDDMockito.then(deleteClubUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("delete-club(No Access Token)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임_ID_가_없는_경우_404를_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(deleteClubUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        delete(DELETE_CLUB_URL, 1L)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andExpect(status().isNotFound());

        BDDMockito.then(deleteClubUseCase).should().command(any());
        resultActions.andDo(
                document("delete-club(Nonexistent Club ID)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_회장이_아닌_경우_403을_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        doThrow(new ClubException(NO_AUTHORITY_DELETE_CLUB))
                .when(deleteClubUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                delete(DELETE_CLUB_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isForbidden());

        // when & then
        BDDMockito.then(deleteClubUseCase).should().command(any());

        resultActions.andDo(
                document("delete-club(requester does not president)",
                        getDocumentResponse()
                )
        );
    }
}