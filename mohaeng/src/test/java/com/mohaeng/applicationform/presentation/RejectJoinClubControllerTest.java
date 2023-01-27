package com.mohaeng.applicationform.presentation;

import com.mohaeng.applicationform.application.usecase.RejectJoinClubUseCase;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.*;
import static com.mohaeng.applicationform.presentation.RejectJoinClubController.REJECT_JOIN_CLUB_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
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

@DisplayName("RejectJoinClubController 는 ")
@WebMvcTest(controllers = RejectJoinClubController.class)
class RejectJoinClubControllerTest extends ControllerTest {

    @MockBean
    private RejectJoinClubUseCase rejectJoinClubUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임 가입 신청 거절 성공")
        void success_test_1() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long applicationFormId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REJECT_JOIN_CLUB_URL, applicationFormId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            // when & then
            verify(rejectJoinClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("reject-join-club-application",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("applicationFormId").description("가입 신청서 ID")
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("처리자가 임원진이 아닌 경우 403을 반환한다.")
        void fail_test_1() throws Exception {
            // given
            doThrow(new ApplicationFormException(NO_AUTHORITY_PROCESS_APPLICATION_FORM))
                    .when(rejectJoinClubUseCase).command(any());
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long applicationFormId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REJECT_JOIN_CLUB_URL, applicationFormId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());

            // when & then
            verify(rejectJoinClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("reject-join-club-application fail(no authority)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("이미 처리된 신청서인 경우 400을 반환한다.")
        void fail_test_2() throws Exception {
            // given
            doThrow(new ApplicationFormException(ALREADY_PROCESSED_APPLICATION_FORM))
                    .when(rejectJoinClubUseCase).command(any());
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long applicationFormId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REJECT_JOIN_CLUB_URL, applicationFormId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // when & then
            verify(rejectJoinClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("reject-join-club-application fail(already processed)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("없는 신청서의 경우 404를 반환한다.")
        void fail_test_3() throws Exception {
            // given
            doThrow(new ApplicationFormException(NOT_FOUND_APPLICATION_FORM))
                    .when(rejectJoinClubUseCase).command(any());
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long applicationFormId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REJECT_JOIN_CLUB_URL, applicationFormId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());

            // when & then
            verify(rejectJoinClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("reject-join-club-application fail(no application form)",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
        void fail_test_4() throws Exception {
            // given
            final Long applicationFormId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(REJECT_JOIN_CLUB_URL, applicationFormId)
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            // when & then
            verify(rejectJoinClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("reject-join-club-application fail(No Access Token)",
                            getDocumentResponse()
                    )
            );
        }
    }
}