package com.mohaeng.notification.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mohaeng.common.fixtures.NotificationFixture;
import com.mohaeng.common.presentation.ControllerTest;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryAllNotificationUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.NotificationResponseTestImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.notification.domain.repository.NotificationQueryRepository.NotificationFilter.ReadFilter.*;
import static com.mohaeng.notification.presentation.QueryAllNotificationController.QUERY_ALL_NOTIFICATION_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryAllNotificationController(알림 전체 조회 컨트롤러) 은")
@WebMvcTest(QueryAllNotificationController.class)
class QueryAllNotificationControllerTest extends ControllerTest {

    private final long memberId = 1;
    private Page<NotificationDto> readNotifications;
    private Page<NotificationDto> unreadNotifications;
    private Page<NotificationDto> allNotifications;

    @MockBean
    private QueryAllNotificationUseCase queryAllNotificationUseCase;

    private final Pageable pageable = PageRequest.of(100, 100);

    @BeforeEach
    void init() {
        List<NotificationDto> reads = List.of(NotificationFixture.expelParticipantNotification(1L, memberId), NotificationFixture.applicationProcessedNotification(2L, memberId)).stream().peek(Notification::read).map(Notification::toDto).toList();
        List<NotificationDto> unReads = List.of(NotificationFixture.officerApproveApplicationNotification(34L, memberId), NotificationFixture.fillOutApplicationFormNotification(62L, memberId)).stream().map(Notification::toDto).toList();
        List<NotificationDto> all = new ArrayList<>();
        all.addAll(reads);
        all.addAll(unReads);

        readNotifications = new PageImpl<>(reads, PageRequest.of(0, 100), reads.size());
        unreadNotifications = new PageImpl<>(unReads, PageRequest.of(0, 100), unReads.size());
        allNotifications = new PageImpl<>(all, PageRequest.of(0, 100), all.size());

        given(queryAllNotificationUseCase.query(
                refEq(new QueryAllNotificationUseCase.Query(new NotificationQueryRepository.NotificationFilter(memberId, ALL), pageable), "pageable"))
        ).willReturn(allNotifications);

        given(queryAllNotificationUseCase.query(
                refEq(new QueryAllNotificationUseCase.Query(new NotificationQueryRepository.NotificationFilter(memberId, ONLY_READ), pageable), "pageable"))
        ).willReturn(readNotifications);

        given(queryAllNotificationUseCase.query(
                refEq(new QueryAllNotificationUseCase.Query(new NotificationQueryRepository.NotificationFilter(memberId, ONLY_UNREAD), pageable), "pageable"))
        ).willReturn(unreadNotifications);
    }

    @Test
    void 필터링_조건에_ALL_을_달았다면_자신의_전체_알림을_조회한다() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_ALL_NOTIFICATION_URL + "?readFilter=ALL")
                .login(memberId)
                .noContent()
                .ok();

        // then
        MvcResult mvcResult = resultActions.andReturn();
        CommonResponse<List<NotificationResponseTestImpl>> notificationResponses = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<CommonResponse<List<NotificationResponseTestImpl>>>() {
        });
        assertThat(notificationResponses.data().stream().filter(NotificationResponseTestImpl::isRead).count()).isEqualTo(readNotifications.getTotalElements());
        assertThat(notificationResponses.data().stream().filter(it -> !it.isRead()).count()).isEqualTo(unreadNotifications.getTotalElements());
        resultActions.andDo(document("notification/query/all/filter ALL",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                )
        ));
    }

    @Test
    void 필터링_조건에_아무것도_달지_않았다면_자신의_전체_알림을_조회한다() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_ALL_NOTIFICATION_URL)
                .login(memberId)
                .noContent()
                .ok();

        // then

        MvcResult mvcResult = resultActions.andReturn();
        CommonResponse<List<NotificationResponseTestImpl>> notificationResponses = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<CommonResponse<List<NotificationResponseTestImpl>>>() {
        });
        assertThat(notificationResponses.data().size()).isEqualTo(readNotifications.getTotalElements() + unreadNotifications.getTotalElements());
        assertThat(notificationResponses.data().stream().filter(NotificationResponseTestImpl::isRead).count()).isEqualTo(readNotifications.getTotalElements());
        assertThat(notificationResponses.data().stream().filter(it -> !it.isRead()).count()).isEqualTo(unreadNotifications.getTotalElements());
        resultActions.andDo(document("notification/query/all/no filter",
                getDocumentRequest(),
                getDocumentResponse()
        ));
    }

    @Test
    void 필터링_조건에_ONLY_READ_를_달았다면_자신의_읽은_알림만을_조회할_수_있다() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_ALL_NOTIFICATION_URL + "?readFilter=ONLY_READ")
                .login(memberId)
                .noContent()
                .ok();

        // then
        MvcResult mvcResult = resultActions.andReturn();
        CommonResponse<List<NotificationResponseTestImpl>> notificationResponses = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<CommonResponse<List<NotificationResponseTestImpl>>>() {
        });
        assertThat(notificationResponses.data().size()).isEqualTo(readNotifications.getTotalElements());
        assertThat(notificationResponses.data().stream().filter(NotificationResponse::isRead).count()).isEqualTo(readNotifications.getTotalElements());
        resultActions.andDo(document("notification/query/all/filter ONLY_READ",
                getDocumentRequest(),
                getDocumentResponse()
        ));
    }

    @Test
    void 필터링_조건에_ONLY_UNREAD_를_달았다면_자신의_읽지않은_알림만을_조회할_수_있다() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_ALL_NOTIFICATION_URL + "?readFilter=ONLY_UNREAD")
                .login(memberId)
                .noContent()
                .ok();

        // then
        MvcResult mvcResult = resultActions.andReturn();
        CommonResponse<List<NotificationResponseTestImpl>> notificationResponses = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<CommonResponse<List<NotificationResponseTestImpl>>>() {
        });
        assertThat(notificationResponses.data().size()).isEqualTo(unreadNotifications.getTotalElements());
        assertThat(notificationResponses.data().stream().filter(it -> !it.isRead()).count()).isEqualTo(unreadNotifications.getTotalElements());
        resultActions.andDo(document("notification/query/all/filter ONLY_UNREAD",
                getDocumentRequest(),
                getDocumentResponse()
        ));
    }

    @ParameterizedTest(name = "readFilter 는 [{0}] 이 들어오면 오류가 발생한다")
    @ValueSource(strings = {
            "onlyUnread",
            "ONLYUNREAD",
            "ONLY UNREAD",
            "only_unread",
    })
    void 카멜_케이스나_소문자_케이스를_테스트한다(final String value) throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_ALL_NOTIFICATION_URL + "?readFilter=" + value)
                .login(memberId)
                .noContent()
                .badRequest();
        // then
    }

    @Test
    void 로그인_하지_않은_경우_401를_반환한다() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_ALL_NOTIFICATION_URL)
                .noLogin()
                .noContent()
                .unAuthorized();

        // then
        resultActions.andDo(document("notification/query/all/fail/no access token",
                getDocumentRequest(),
                getDocumentResponse()
        ));
    }
}