package com.mohaeng.notification.application.service.query;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.common.fixtures.NotificationFixture;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryNotificationByIdUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.notification.exception.NotificationException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.mohaeng.common.util.RepositoryUtil.saveNotifications;
import static com.mohaeng.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryNotificationById(알림 단일 조회) 는")
@ApplicationTest
class QueryNotificationByIdTest {

    @Autowired
    private QueryNotificationByIdUseCase queryNotificationByIdUseCase;

    @Autowired
    private EntityManager em;

    @Autowired
    private NotificationRepository notificationRepository;

    private List<Notification> notifications = new ArrayList<>();

    @BeforeEach
    void init() {
        notifications = saveNotifications(notificationRepository, NotificationFixture.allKindNotifications());
        em.flush();
        em.clear();
        System.out.println("========== AFTER SETTING ==========");
    }

    @Test
    void id로_모임_단일_조회_시_읽음_처리하고_반환한다() {
        // when
        notifications.forEach(it -> {

            assertThat(it.isRead()).isFalse();

            NotificationDto notificationDto = queryNotificationByIdUseCase.query(
                    new QueryNotificationByIdUseCase.Query(it.receiver().receiverId(), it.id())
            );

            // then
            assertAll(
                    () -> assertThat(notificationDto.id()).isEqualTo(it.id()),
                    () -> assertThat(notificationDto.isRead()).isTrue(),
                    () -> assertThat(notificationDto.type()).isEqualTo(it.getClass().getSimpleName())
            );
        });
    }

    @Test
    void 알림이_없는_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(NotificationException.class, () ->
                queryNotificationByIdUseCase.query(
                        new QueryNotificationByIdUseCase.Query(notifications.get(0).receiver().receiverId(), 1213123123L)
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_NOTIFICATION);
    }

    @Test
    void 본인의_알림이_아닌_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(NotificationException.class, () ->
                queryNotificationByIdUseCase.query(
                        new QueryNotificationByIdUseCase.Query(notifications.get(0).receiver().receiverId() + 100L, notifications.get(0).id())
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_NOTIFICATION);
    }
}