package com.mohaeng.club.presentation;

import com.mohaeng.club.application.usecase.DeleteClubUseCase;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.exception.ClubExceptionType.NO_AUTHORITY_DELETE_CLUB;
import static com.mohaeng.club.presentation.DeleteClubController.DELETE_CLUB_URL;
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

@WebMvcTest(DeleteClubController.class)
class DeleteClubControllerTest extends ControllerTest {

    @MockBean
    private DeleteClubUseCase deleteClubUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임의 회장이 모임 제거 요청을 보낸 경우 모임을 제거하고 200을 반환한다.")
        void success_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(deleteClubUseCase, times(1)).command(any());

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
                            delete(DELETE_CLUB_URL, 1L)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(deleteClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("delete-club(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("모임 ID가 없는 경우 404 를 반환한다.")
        void fail_test_2() throws Exception {
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
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(deleteClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club(Nonexistent Club ID)",
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
            doThrow(new ClubException(NO_AUTHORITY_DELETE_CLUB))
                    .when(deleteClubUseCase).command(any());

            setAuthentication(memberId);

            ResultActions resultActions = mockMvc.perform(
                            delete(DELETE_CLUB_URL, 1L)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    ).andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(deleteClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("delete-club(requester does not president)",
                            getDocumentResponse()
                    )
            );
        }
    }
}