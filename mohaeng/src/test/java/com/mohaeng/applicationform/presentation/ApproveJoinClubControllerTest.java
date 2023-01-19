package com.mohaeng.applicationform.presentation;

import com.mohaeng.applicationform.application.usecase.ApproveJoinClubUseCase;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.*;
import static com.mohaeng.applicationform.presentation.ApproveJoinClubController.APPROVE_JOIN_CLUB_URL;
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

@DisplayName("ApproveJoinClubController 는 ")
@WebMvcTest(controllers = ApproveJoinClubController.class)
class ApproveJoinClubControllerTest extends ControllerTest {

    /**
     * 모임 가입 신청 수락 성공
     * <p>
     * 모임 가입 신청 수락 실패 1 : AccessToken 없음
     * 모임 가입 신청 수락 실패 2 : 임원진이 아님
     * 모임 가입 신청 수락 실패 3 : 이미 가입된 회원임
     * 모임 가입 신청 수락 실패 4 : 이미 처리된 신청서임
     * 모임 가입 신청 수락 실패 5 : 신청서 없음
     */

    @MockBean
    private ApproveJoinClubUseCase approveJoinClubUseCase;

    @Test
    @DisplayName("모임 가입 신청 수락 성공")
    void success_test_1() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        final Long applicationFormId = 1L;
        ResultActions resultActions = mockMvc.perform(
                        post(APPROVE_JOIN_CLUB_URL, applicationFormId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // when & then
        verify(approveJoinClubUseCase, times(1)).command(any());

        resultActions.andDo(
                document("approve-join-club-application",
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

    @Test
    @DisplayName("처리자가 임원진이 아닌 경우 403을 반환한다.")
    void fail_test_2() throws Exception {
        // given
        doThrow(new ApplicationFormException(NO_AUTHORITY_PROCESS_APPLICATION_FORM))
                .when(approveJoinClubUseCase).command(any());
        final Long memberId = 1L;
        setAuthentication(memberId);
        final Long applicationFormId = 1L;
        ResultActions resultActions = mockMvc.perform(
                        post(APPROVE_JOIN_CLUB_URL, applicationFormId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isForbidden());

        // when & then
        verify(approveJoinClubUseCase, times(1)).command(any());

        resultActions.andDo(
                document("approve-join-club-application fail(no authority)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    @DisplayName("이미 처리된 신청서인 경우 400을 반환한다.")
    void fail_test_3() throws Exception {
        // given
        doThrow(new ApplicationFormException(ALREADY_PROCESSED_APPLICATION_FORM))
                .when(approveJoinClubUseCase).command(any());
        final Long memberId = 1L;
        setAuthentication(memberId);
        final Long applicationFormId = 1L;
        ResultActions resultActions = mockMvc.perform(
                        post(APPROVE_JOIN_CLUB_URL, applicationFormId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        // when & then
        verify(approveJoinClubUseCase, times(1)).command(any());

        resultActions.andDo(
                document("approve-join-club-application fail(already processed)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    @DisplayName("없는 신청서의 경우 404를 반환한다.")
    void fail_test_1() throws Exception {
        // given
        doThrow(new ApplicationFormException(NOT_FOUND_APPLICATION_FORM))
                .when(approveJoinClubUseCase).command(any());
        final Long memberId = 1L;
        setAuthentication(memberId);
        final Long applicationFormId = 1L;
        ResultActions resultActions = mockMvc.perform(
                        post(APPROVE_JOIN_CLUB_URL, applicationFormId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        // when & then
        verify(approveJoinClubUseCase, times(1)).command(any());

        resultActions.andDo(
                document("approve-join-club-application fail(no application form)",
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
                        post(APPROVE_JOIN_CLUB_URL, applicationFormId)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // when & then
        verify(approveJoinClubUseCase, times(0)).command(any());

        resultActions.andDo(
                document("approve-join-club-application fail(No Access Token)",
                        getDocumentResponse()
                )
        );
    }
}