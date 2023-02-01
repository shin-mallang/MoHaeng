package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.participant.domain.event.ClubJoinApplicationCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
@DisplayName("NotificationEventHandler 는 ")
class NotificationEventHandlerAsyncTest {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private NotificationEventHandler eventHandler;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("알림 관련 이벤트를 받아 알림을 생성하여 저장한다.")
        void success_test_1() {
            // given
            List<Long> memberIds = List.of(1L, 2L, 3L, 4L);
            eventHandler.handle(new ClubJoinApplicationCreatedEvent(this, memberIds, 10L, 11L, 12L));

            // when
            List<Notification> all = repository.findAll();
            all.stream().forEach(it -> System.out.println(it.receiver().receiverId()));

            // then
            assertThat(all.size()).isEqualTo(memberIds.size());
        }
    }
}