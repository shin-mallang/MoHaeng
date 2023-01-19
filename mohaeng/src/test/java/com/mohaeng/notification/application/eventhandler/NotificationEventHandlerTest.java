package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.applicationform.domain.event.ClubJoinApplicationCreatedEvent;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
@DisplayName("NotificationEventHandler 는 ")
class NotificationEventHandlerTest {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private NotificationEventHandler eventHandler;

    @Test
    @DisplayName("알림 관련 이벤트를 받아 알림을 생성하여 저장한다.")
    void test() {
        // given
        List<Long> memberIds = List.of(1L, 2L, 3L, 4L);

        // when
        eventHandler.handle(new ClubJoinApplicationCreatedEvent(this, memberIds, 10L, 11L, 12L));

        // then
        List<Notification> all = repository.findAll();
        assertThat(all.size()).isEqualTo(memberIds.size());
    }
}