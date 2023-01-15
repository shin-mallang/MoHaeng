package com.mohaeng.alarm.presentation;

import com.mohaeng.alarm.application.usecase.QueryAlarmByIdUseCase;
import com.mohaeng.alarm.exception.AlarmException;
import com.mohaeng.common.ControllerTest;
import com.mohaeng.common.fixtures.AlarmFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.alarm.exception.AlarmExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.alarm.presentation.QueryAlarmByIdController.QUERY_ALARM_BY_ID;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("QueryAlarmByIdController 는 ")
@WebMvcTest(QueryAlarmByIdController.class)
class QueryAlarmByIdControllerTest extends ControllerTest {

    @MockBean
    private QueryAlarmByIdUseCase queryAlarmByIdUseCase;

    @Test
    @DisplayName("인증된 사용자가 자신이 받은 알람 중 하나를 올바르게 조회하는 경우 알람의 정보를 반환한다.")
    void success_test_1() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryAlarmByIdUseCase.query(any())).thenReturn(AlarmFixture.alarmDto());
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(queryAlarmByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("alarm-query-by-id",
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
    @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
    void fail_test_1() throws Exception {
        // given
        final Long memberId = 1L;
        final Long alarmId = 1L;
        when(queryAlarmByIdUseCase.query(any())).thenReturn(AlarmFixture.alarmDto());
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID, alarmId)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // when & then
        verify(queryAlarmByIdUseCase, times(0)).query(any());

        resultActions.andDo(
                document("alarm-query-by-id fail(No Access Token)",
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
        when(queryAlarmByIdUseCase.query(any())).
                thenThrow(new AlarmException(NOT_FOUND_APPLICATION_FORM));
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        // when & then
        verify(queryAlarmByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("alarm-query-by-id fail(alarm's receiver id is not matched login member id)",
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
        when(queryAlarmByIdUseCase.query(any())).
                thenThrow(new AlarmException(NOT_FOUND_APPLICATION_FORM));
        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                        get(QUERY_ALARM_BY_ID, alarmId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andDo(print())
                .andExpect(status().isNotFound());

        // when & then
        verify(queryAlarmByIdUseCase, times(1)).query(any());

        resultActions.andDo(
                document("alarm-query-by-id fail(alarm does not exist)",
                        getDocumentResponse()
                )
        );
    }
}