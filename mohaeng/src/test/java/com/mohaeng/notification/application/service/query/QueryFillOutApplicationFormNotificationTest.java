package com.mohaeng.notification.application.service.query;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.type.FillOutApplicationFormNotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryFillOutApplicationFormNotificationUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.type.FillOutApplicationFormNotification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.mohaeng.common.fixtures.NotificationFixture.allKindNotificationsWithReceiverId;
import static com.mohaeng.common.fixtures.NotificationFixture.fillOutApplicationFormNotification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryFillOutApplicationFormNotification(모임 가입 신청 알림 전부 조회i) 은")
@ApplicationTest
class QueryFillOutApplicationFormNotificationTest {

    @Autowired
    private QueryFillOutApplicationFormNotificationUseCase queryFillOutApplicationFormNotificationUseCase;

    @Autowired
    private EntityManager em;

    @Autowired
    private NotificationRepository notificationRepository;

    private Long myId = 1L;

    private List<Notification> myAllNotifications;
    private List<Notification> myAllFillOutApplicationFormNotifications;
    private List<Notification> myReadFillOutApplicationFormNotifications;
    private List<Notification> myUnReadFillOutApplicationFormNotifications;
    private List<Notification> otherAllNotifications;

    @BeforeEach
    void init() {
        myAllNotifications = new ArrayList<>(allKindNotificationsWithReceiverId(myId));
        myAllNotifications.addAll(List.of(fillOutApplicationFormNotification(myId), fillOutApplicationFormNotification(myId), fillOutApplicationFormNotification(myId)));
        myAllNotifications.addAll(Stream.of(fillOutApplicationFormNotification(myId), fillOutApplicationFormNotification(myId), fillOutApplicationFormNotification(myId)).peek(Notification::read).toList());
        notificationRepository.saveAll(myAllNotifications);

        myAllFillOutApplicationFormNotifications = myAllNotifications.stream().filter(it -> it instanceof FillOutApplicationFormNotification).toList();
        myReadFillOutApplicationFormNotifications = myAllFillOutApplicationFormNotifications.stream().filter(Notification::isRead).toList();
        myUnReadFillOutApplicationFormNotifications = myAllFillOutApplicationFormNotifications.stream().filter(it -> !it.isRead()).toList();

        otherAllNotifications = allKindNotificationsWithReceiverId(11L);
        notificationRepository.saveAll(otherAllNotifications);
        em.flush();
        em.clear();
    }

    @Test
    void 나에게_온_모임_가입_신청_요청들을_조회한다() {
        // when
        Page<FillOutApplicationFormNotificationDto> query = 모임_가입_신청_요청들을_조회한다();

        // then
        assertAll(
                () -> assertThat(query.getTotalElements()).isEqualTo(myAllFillOutApplicationFormNotifications.size()),
                () -> assertThat(query.getTotalElements()).isNotEqualTo(myAllNotifications.size() + otherAllNotifications.size())
        );
    }

    @Test
    void 읽음_안읽음_여부에_상관없이_모두_조회되어야_한다() {
        // when
        Page<FillOutApplicationFormNotificationDto> query = 모임_가입_신청_요청들을_조회한다();

        // then
        List<FillOutApplicationFormNotificationDto> unReads = query.stream().filter(it -> !it.isRead()).toList();
        List<FillOutApplicationFormNotificationDto> reads = query.stream().filter(NotificationDto::isRead).toList();
        assertAll(
                () -> assertThat(unReads.size()).isEqualTo(myUnReadFillOutApplicationFormNotifications.size()),
                () -> assertThat(reads.size()).isEqualTo(myReadFillOutApplicationFormNotifications.size())
        );
    }

    private Page<FillOutApplicationFormNotificationDto> 모임_가입_신청_요청들을_조회한다() {
        return queryFillOutApplicationFormNotificationUseCase.query(
                new QueryFillOutApplicationFormNotificationUseCase.Query(myId, PageRequest.of(0, 100))
        );
    }
}