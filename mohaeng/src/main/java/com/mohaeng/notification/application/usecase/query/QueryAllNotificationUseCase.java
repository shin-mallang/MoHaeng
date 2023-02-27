package com.mohaeng.notification.application.usecase.query;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository.NotificationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryAllNotificationUseCase {

    Page<NotificationDto> query(final Query query);

    record Query(
            NotificationFilter filter,
            Pageable pageable
    ) {
    }
}
