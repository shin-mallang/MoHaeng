package com.mohaeng.applicationform.presentation;

import com.mohaeng.applicationform.application.usecase.RequestJoinClubUseCase;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_REQUEST_JOIN_CLUB;
import static com.mohaeng.applicationform.presentation.RequestJoinClubController.REQUEST_JOIN_CLUB_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
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

@DisplayName("RequestJoinClubController 는 ")
@WebMvcTest(controllers = RequestJoinClubController.class)
class RequestJoinClubControllerTest extends ControllerTest {

    @MockBean
    private RequestJoinClubUseCase requestJoinClubUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임 가입 신청 성공 시 200과, 신청하였다는 메세지를 반환한다.")
        void success_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REQUEST_JOIN_CLUB_URL, clubId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            // when & then
            verify(requestJoinClubUseCase, times(1)).command(any());

            assertThat(resultActions.andReturn().getResponse().getContentAsString()).isEqualTo("가입 신청 요청을 보냈습니다.");

            resultActions.andDo(
                    document("request-join-club",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("clubId").description("모임 ID")
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("이미 가입 신청을 보냈으며, 해당 요청이 처리되지 않았는데 재요청한 경우 400을 반환한다.")
        void fail_test_1() throws Exception {
            // given
            when(requestJoinClubUseCase.command(any())).thenThrow(new ApplicationFormException(ALREADY_REQUEST_JOIN_CLUB));
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REQUEST_JOIN_CLUB_URL, clubId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(requestJoinClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("request-join-club fail(already request join club)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("이미 모임에 가입한 사람의 경우 400을 반환한다.")
        void fail_test_2() throws Exception {
            // given
            when(requestJoinClubUseCase.command(any())).thenThrow(new ApplicationFormException(ALREADY_REQUEST_JOIN_CLUB));
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REQUEST_JOIN_CLUB_URL, clubId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(requestJoinClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("request-join-club fail(member already joined club)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
        void fail_test_3() throws Exception {
            // given
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REQUEST_JOIN_CLUB_URL, clubId)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            // when & then
            verify(requestJoinClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("request-join-club fail(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }
    }
}