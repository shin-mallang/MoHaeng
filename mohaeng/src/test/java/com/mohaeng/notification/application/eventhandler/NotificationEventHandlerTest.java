package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.applicationform.domain.event.ClubJoinApplicationRequestedEvent;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
@DisplayName("NotificationEventHandler 는 ")
class NotificationEventHandlerTest {

    @Autowired
    private NotificationEventHandler eventHandler;

    @Autowired
    private NotificationRepository repository;

    @Test
    @DisplayName("알림 관련 이벤트를 받아 알림을 생성하여 저장한다.")
    void test() {
        // given
        List<Long> memberIds = List.of(1L, 2L, 3L, 4L);
        eventHandler.handle(new ClubJoinApplicationRequestedEvent(this, memberIds, 10L, 11L, 12L));

        // when
        List<Notification> all = repository.findAll();

        // then
        assertThat(all.size()).isEqualTo(memberIds.size());
    }
}