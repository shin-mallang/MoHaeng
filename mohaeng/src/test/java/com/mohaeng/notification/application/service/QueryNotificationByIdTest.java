package com.mohaeng.notification.application.service;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.notification.application.dto.kind.ApplicationProcessedNotificationDto;
import com.mohaeng.notification.application.usecase.QueryNotificationByIdUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.ApplicationProcessedNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.notification.exception.NotificationException;
import com.mohaeng.notification.exception.NotificationExceptionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ApplicationTest
@DisplayName("QueryNotificationById 는")
class QueryNotificationByIdTest {

    @Autowired
    private QueryNotificationByIdUseCase queryNotificationByIdUseCase;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("자신의 알림인 경우 조회 가능하다.")
        void success_test_1() {
            // given
            Member member = memberRepository.save(member(null));
            ApplicationProcessedNotification notification = (ApplicationProcessedNotification) notificationRepository.save(new ApplicationProcessedNotification(Receiver.of(member.id()), 1L, false));

            // when
            ApplicationProcessedNotificationDto notificationDto = (ApplicationProcessedNotificationDto) queryNotificationByIdUseCase.query(
                    new QueryNotificationByIdUseCase.Query(notification.id(), member.id())
            );

            // then
            assertAll(
                    () -> assertThat(notificationDto.id()).isEqualTo(notification.id()),
                    () -> assertThat(notificationDto.isRead()).isEqualTo(notification.isRead()),
                    () -> assertThat(notificationDto.isRead()).isTrue(),
                    () -> assertThat(notificationDto.createdAt()).isEqualTo(notification.createdAt()),
                    () -> assertThat(notificationDto.type()).isEqualTo(notification.getClass().getSimpleName()),
                    () -> assertThat(notificationDto.clubId()).isEqualTo(notification.clubId()),
                    () -> assertThat(notificationDto.isApproved()).isEqualTo(notification.isApproved())
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("자신의 알림이 아닌 경우 조회 불가능하다.")
        void fail_test_1() {
            // given
            Member member = memberRepository.save(member(null));
            Member another = memberRepository.save(member(null));
            ApplicationProcessedNotification notification = (ApplicationProcessedNotification) notificationRepository.save(new ApplicationProcessedNotification(Receiver.of(member.id()), 1L, false));

            // when
            BaseExceptionType baseExceptionType = Assertions.assertThrows(NotificationException.class, () -> queryNotificationByIdUseCase.query(
                    new QueryNotificationByIdUseCase.Query(notification.id(), another.id())
            )).exceptionType();

            Notification find = notificationRepository.findById(notification.id()).orElse(null);
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NotificationExceptionType.NOT_FOUND_NOTIFICATION),
                    () -> assertThat(find.isRead()).isFalse()
            );
        }

        @Test
        @DisplayName("알림이 없는 경우 조회할 수 없다.")
        void fail_test_2() {
            // when
            BaseExceptionType baseExceptionType = Assertions.assertThrows(NotificationException.class, () -> queryNotificationByIdUseCase.query(
                    new QueryNotificationByIdUseCase.Query(1L, 1L)
            )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NotificationExceptionType.NOT_FOUND_NOTIFICATION)
            );
        }
    }
}