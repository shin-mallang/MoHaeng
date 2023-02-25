package com.mohaeng.notification.application.usecase.query;

import com.mohaeng.notification.application.dto.NotificationDto;

public interface QueryNotificationByIdUseCase {

    NotificationDto query(final Query query);

    record Query(
            Long id
    ) {
    }
}
