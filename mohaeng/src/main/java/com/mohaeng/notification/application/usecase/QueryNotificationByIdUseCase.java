package com.mohaeng.notification.application.usecase;

import com.mohaeng.notification.application.usecase.dto.NotificationDto;

public interface QueryNotificationByIdUseCase {

    NotificationDto query(final Query query);

    /**
     * 자신의 것이 아니면 볼 수 없으므로 member id 도 필요하다
     */
    record Query(
            Long notificationId,
            Long memberId
    ) {
    }
}
