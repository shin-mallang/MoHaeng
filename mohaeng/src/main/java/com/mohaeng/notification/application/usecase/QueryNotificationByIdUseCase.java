package com.mohaeng.notification.application.usecase;

import com.mohaeng.notification.application.usecase.dto.NotificationDto;

public interface QueryNotificationByIdUseCase {

    NotificationDto query(final Query query);

    record Query(
            Long alarmId,
            Long memberId
    ) {
    }
}
