package com.mohaeng.notification.application.service.query;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.fixtures.NotificationFixture;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryAllNotificationUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository.NotificationFilter;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mohaeng.notification.domain.repository.NotificationQueryRepository.NotificationFilter.ReadFilter.*;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryAllNotification(전체 알림 조회) 은")
@ApplicationTest
class QueryAllNotificationTest {

    @Autowired
    private QueryAllNotificationUseCase queryAllNotificationUseCase;

    @Autowired
    private EntityManager em;

    @Autowired
    private NotificationRepository notificationRepository;

    private final Long myId = 1L;
    private final Long otherId = 111L;
    private List<Notification> unread;
    private List<Notification> read;

    @BeforeEach
    void init() {
        // 각각 8개씩 저장한다
        unread = new ArrayList<>(NotificationFixture.noIdAllKindNotifications(myId));
        read = new ArrayList<>(NotificationFixture.noIdAllKindNotifications(myId));
        read.remove(0);
        read.forEach(Notification::read);
        unread = notificationRepository.saveAll(unread);
        read = notificationRepository.saveAll(read);
    }

    @Test
    void 읽은_알림만_조회할_수_있다() {
        // when
        Page<NotificationDto> result = queryAllNotificationUseCase.query(new QueryAllNotificationUseCase.Query(
                new NotificationFilter(myId, ONLY_READ), PageRequest.of(0, 100))
        );

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(read.size());
        assertThat(result.map(NotificationDto::isRead).stream().distinct().collect(Collectors.toList())).containsOnly(true);
    }

    @Test
    void 안읽은_알림만_조회할_수_있다() {
        // when
        Page<NotificationDto> result = queryAllNotificationUseCase.query(
                new QueryAllNotificationUseCase.Query(new NotificationFilter(myId, ONLY_UNREAD), PageRequest.of(0, 100))
        );

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(unread.size());
        assertThat(result.map(NotificationDto::isRead).stream().distinct().collect(Collectors.toList())).containsOnly(false);
    }

    @Test
    void 모든_알림을_조회할_수_있다() {
        // when
        Page<NotificationDto> result = queryAllNotificationUseCase.query(new QueryAllNotificationUseCase.Query(
                new NotificationFilter(myId, ALL), PageRequest.of(0, 100))
        );

        // then
        assertThat(result.getNumberOfElements()).isEqualTo(read.size() + unread.size());
    }

    @Test
    void 내_알림이_아니면_조회되지_않는다() {
        // given
        List<Notification> otherRead = new ArrayList<>(NotificationFixture.noIdAllKindNotifications(otherId));
        List<Notification> otherUnRead = new ArrayList<>(NotificationFixture.noIdAllKindNotifications(otherId));
        otherRead.forEach(Notification::read);
        otherUnRead = notificationRepository.saveAll(otherUnRead);
        otherRead = notificationRepository.saveAll(otherRead);

        // when
        Page<NotificationDto> result = queryAllNotificationUseCase.query(new QueryAllNotificationUseCase.Query(
                new NotificationFilter(otherId, ALL), PageRequest.of(0, 100))
        );

        // then - 다른 사람의 알림을 추가하고 읽어도 결과는 자신의 개수와만 동일함
        assertThat(result.getNumberOfElements()).isNotEqualTo(read.size() + unread.size() + otherRead.size() + otherUnRead.size());
        assertThat(result.getNumberOfElements()).isEqualTo(otherRead.size() + otherUnRead.size());
    }
}