package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategies;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.notification.mock.MockNotificationConfig;
import com.mohaeng.notification.mock.MockNotificationEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({MockNotificationConfig.class})
@DisplayName("NotificationEventHandler 는")
class NotificationEventHandlerTest {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private EventHistoryRepository eventHistoryRepository;

    @Autowired
    private NotificationMakeStrategies notificationMakeStrategies;

    // Autowired로 받으면 Async라 다른 테스트에서 오류 발생
    private NotificationEventHandler eventHandler;

    @BeforeEach
    void init() {
        eventHandler = new NotificationEventHandler(eventHistoryRepository, repository, notificationMakeStrategies);
    }

    @Test
    @DisplayName("알림 관련 이벤트를 받아 알림을 생성하여 저장한다.")
    void success_test_1() {
        // given
        List<Long> longs = List.of(1L, 2L);
        eventHandler.handle(new MockNotificationEvent(this, longs));

        // when
        List<Notification> all = repository.findAll();

        // then
        assertThat(all.size()).isEqualTo(longs.size());
    }
}