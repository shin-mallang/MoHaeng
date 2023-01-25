package com.mohaeng.notification.presentation;

import com.mohaeng.common.ControllerTest;
import com.mohaeng.notification.application.usecase.QueryNotificationByIdUseCase;
import com.mohaeng.notification.exception.NotificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static com.mohaeng.common.fixtures.NotificationFixture.*;
import static com.mohaeng.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;
import static com.mohaeng.notification.presentation.QueryNotificationByIdController.QUERY_ALARM_BY_ID_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("QueryNotificationByIdController 는 ")
@WebMvcTest(QueryNotificationByIdController.class)
class QueryNotificationByIdControllerTest extends ControllerTest {

    @MockBean
    private QueryNotificationByIdUseCase queryNotificationByIdUseCase;

    @Test
    @DisplayName("인증된 사용자의 자신이 받은 알림 조회 성공 (applicationProcessedNotificationDto)")
    void success_test_1_applicationProcessedNotification() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).thenReturn(applicationProcessedNotificationDto(1L));
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(queryNotificationByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("notification-query-by-id(applicationProcessedNotification)",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("알람 ID")
                        )
                )
        );
    }

    @Test
    @DisplayName("인증된 사용자의 자신이 받은 알림 조회 성공 (clubJoinApplicationCreatedNotificationDto)")
    void success_test_2_clubJoinApplicationRequestedNotificationDto() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).thenReturn(clubJoinApplicationCreatedNotificationDto(1L));
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(queryNotificationByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("notification-query-by-id(clubJoinApplicationCreatedNotificationDto)",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("알람 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("알림의 ID"),
                                fieldWithPath("createdAt").type(STRING).description("알림 생성시간"),
                                fieldWithPath("type").type(STRING).description("알림의 종류 - 모임 가입 요청 생성 시 회장과 임원들에게 발송되는 알림"),
                                fieldWithPath("clubId").type(NUMBER).description("가입 신청 대상 모임"),
                                fieldWithPath("applicantId").type(NUMBER).description("모임 가입 신청자의 Member ID"),
                                fieldWithPath("applicationFormId").type(NUMBER).description("생성된 모임 가입 신청서 ID"),
                                fieldWithPath("read").type(BOOLEAN).description("알림 읽음 여부 - true인 경우 읽음")
                        )
                )
        );
    }

    @Test
    @DisplayName("인증된 사용자의 자신이 받은 알림 조회 성공 (officerApproveApplicationNotificationDto)")
    void success_test_3_officerApproveApplicationNotificationDto() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).thenReturn(officerApproveApplicationNotificationDto(1L));
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(queryNotificationByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("notification-query-by-id(officerApproveApplicationNotification)",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("알람 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("알림의 ID"),
                                fieldWithPath("createdAt").type(STRING).description("알림 생성시간"),
                                fieldWithPath("type").type(STRING).description("알림의 종류 - ApplicationProcessedNotification"),
                                fieldWithPath("officerMemberId").type(NUMBER).description("처리한 임원의 Member Id"),
                                fieldWithPath("officerParticipantId").type(NUMBER).description("처리한 임원의 Participant Id"),
                                fieldWithPath("applicantMemberId").type(NUMBER).description("가입된 회원의 Member Id"),
                                fieldWithPath("applicantParticipantId").type(NUMBER).description("가입된 회원의 Participant Id"),
                                fieldWithPath("read").type(BOOLEAN).description("알림 읽음 여부 - true인 경우 읽음")
                        )
                )
        );
    }

    @Test
    @DisplayName("인증된 사용자의 자신이 받은 알림 조회 성공 (officerRejectApplicationNotificationDto)")
    void success_test_4_officerRejectApplicationNotificationDto() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).thenReturn(officerRejectApplicationNotificationDto(1L));
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(queryNotificationByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("notification-query-by-id(officerRejectApplicationNotification)",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("알람 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("알림의 식별자"),
                                fieldWithPath("createdAt").type(STRING).description("알림 생성시간"),
                                fieldWithPath("type").type(STRING).description("알림의 종류 - ApplicationProcessedNotification"),
                                fieldWithPath("officerMemberId").type(NUMBER).description("처리한 임원의 Member Id"),
                                fieldWithPath("officerParticipantId").type(NUMBER).description("처리한 임원의 Participant Id"),
                                fieldWithPath("applicantMemberId").type(NUMBER).description("가입이 거절된 회원의 Member Id"),
                                fieldWithPath("read").type(BOOLEAN).description("알림 읽음 여부 - true인 경우 읽음")
                        )
                )
        );
    }

    @Test
    @DisplayName("인증된 사용자의 자신이 받은 알림 조회 성공 (ExpelParticipantNotification)")
    void success_test_5_expelParticipantNotification() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).thenReturn(expelledParticipantNotificationDto(1L));
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(queryNotificationByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("notification-query-by-id(ExpelParticipantNotification)",
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("알림의 식별자"),
                                fieldWithPath("createdAt").type(STRING).description("알림 생성시간"),
                                fieldWithPath("type").type(STRING).description("알림의 종류 - ApplicationProcessedNotification"),
                                fieldWithPath("clubId").type(NUMBER).description("추방된 모임 ID"),
                                fieldWithPath("read").type(BOOLEAN).description("알림 읽음 여부 - true인 경우 읽음")
                        )
                )
        );
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
    void fail_test_1() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).thenReturn(applicationProcessedNotificationDto(1L));
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // when & then
        verify(queryNotificationByIdUseCase, times(0)).query(any());

        resultActions.andDo(
                document("notification-query-by-id fail(No Access Token)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    @DisplayName("알람의 ID는 존재하지만 회원 자신이 받은 알림이 아닌 경우 404를 반환한다.")
    void fail_test_2() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).
                thenThrow(new NotificationException(NOT_FOUND_NOTIFICATION));
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        // when & then
        verify(queryNotificationByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("notification-query-by-id fail(notification's receiver id is not matched login member id)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    @DisplayName("알람의 ID가 존재하지 않는 경우 경우 404를 반환한다.")
    void fail_test_3() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryNotificationByIdUseCase.query(any())).
                thenThrow(new NotificationException(NOT_FOUND_NOTIFICATION));
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID_URL, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        // when & then
        verify(queryNotificationByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("notification-query-by-id fail(notification does not exist)",
                        getDocumentResponse()
                )
        );
    }
}