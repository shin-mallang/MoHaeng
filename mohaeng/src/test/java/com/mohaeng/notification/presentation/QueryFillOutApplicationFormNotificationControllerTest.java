package com.mohaeng.notification.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mohaeng.common.fixtures.NotificationFixture;
import com.mohaeng.common.presentation.ControllerTest;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.notification.application.dto.type.FillOutApplicationFormNotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryFillOutApplicationFormNotificationUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.type.FillOutApplicationFormNotification;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.type.FillOutApplicationFormNotificationResponse;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.mohaeng.common.fixtures.NotificationFixture.fillOutApplicationFormNotification;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.notification.presentation.QueryFillOutApplicationFormNotificationController.QUERY_FILL_OUT_APPLICATION_FORM_NOTIFICATION_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryFillOutApplicationFormNotificationController(모임 가입 신청 알림 조회 컨틀롤러) 는")
@WebMvcTest(QueryFillOutApplicationFormNotificationController.class)
class QueryFillOutApplicationFormNotificationControllerTest extends ControllerTest {

    @MockBean
    private QueryFillOutApplicationFormNotificationUseCase queryFillOutApplicationFormNotificationUseCase;

    private final Long myId = 1L;

    private List<Notification> myAllNotifications;
    private List<FillOutApplicationFormNotification> myAllFillOutApplicationFormNotifications;
    private List<FillOutApplicationFormNotification> myReadFillOutApplicationFormNotifications;
    private List<FillOutApplicationFormNotification> myUnReadFillOutApplicationFormNotifications;

    @BeforeEach
    void init() {
        myAllNotifications = new ArrayList<>(NotificationFixture.allKindNotificationsWithId(1L, myId));
        long id = 10L;
        myAllNotifications.addAll(List.of(fillOutApplicationFormNotification(id++, myId), fillOutApplicationFormNotification(id++, myId), fillOutApplicationFormNotification(id++, myId)));
        myAllNotifications.addAll(Stream.of(fillOutApplicationFormNotification(id++, myId), fillOutApplicationFormNotification(id++, myId), fillOutApplicationFormNotification(id++, myId))
                .peek(Notification::read).toList());

        myAllFillOutApplicationFormNotifications = myAllNotifications.stream().filter(it -> it instanceof FillOutApplicationFormNotification).map(it -> (FillOutApplicationFormNotification) it).toList();
        myReadFillOutApplicationFormNotifications = myAllFillOutApplicationFormNotifications.stream().filter(Notification::isRead).toList();
        myUnReadFillOutApplicationFormNotifications = myAllFillOutApplicationFormNotifications.stream().filter(it -> !it.isRead()).toList();
        List<FillOutApplicationFormNotificationDto> notificationDtos = myAllFillOutApplicationFormNotifications.stream().map(FillOutApplicationFormNotification::toDto).toList();
        BDDMockito.given(queryFillOutApplicationFormNotificationUseCase.query(any()))
                .willReturn(new PageImpl<>(notificationDtos, PageRequest.of(0, 20), notificationDtos.size()));
    }

    @Test
    void 나에게_온_모임_가입_신청_요청들을_조회한다() throws Exception {
        // given
        ResultActions resultActions = getRequest()
                .url(QUERY_FILL_OUT_APPLICATION_FORM_NOTIFICATION_URL)
                .login()
                .noContent()
                .ok();

        // when
        resultActions.andDo(document("notification/query/모임가입신청알림",
                getDocumentResponse(),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                )
        ));
    }

    @Test
    void 읽음_안읽음_여부에_상관없이_모두_조회되어야_한다() throws Exception {
        // given
        ResultActions resultActions = getRequest()
                .url(QUERY_FILL_OUT_APPLICATION_FORM_NOTIFICATION_URL)
                .login()
                .noContent()
                .ok();

        // when & then
        MvcResult mvcResult = resultActions.andReturn();
        CommonResponse<List<FillOutApplicationFormNotificationResponse>> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertAll(
                () -> assertThat(response.data().size()).isEqualTo(myAllFillOutApplicationFormNotifications.size()),
                () -> assertThat(response.data().stream().filter(NotificationResponse::isRead).count()).isEqualTo(myReadFillOutApplicationFormNotifications.size()),
                () -> assertThat(response.data().stream().filter(it -> !it.isRead()).count()).isEqualTo(myUnReadFillOutApplicationFormNotifications.size())
        );
    }

    @Test
    void 로그인_하지_않은_경우_401를_반환한다() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_FILL_OUT_APPLICATION_FORM_NOTIFICATION_URL)
                .noLogin()
                .noContent()
                .unAuthorized();

        // then
        resultActions.andDo(document("notification/query/모임가입신청알림/fail/no access token",
                getDocumentRequest(),
                getDocumentResponse()
        ));
    }
}