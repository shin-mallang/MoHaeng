package com.mohaeng.notification.application.service.query;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryAllNotificationUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QueryAllNotification implements QueryAllNotificationUseCase {

    private final NotificationQueryRepository notificationQueryRepository;

    public QueryAllNotification(final NotificationQueryRepository notificationQueryRepository) {
        this.notificationQueryRepository = notificationQueryRepository;
    }

    @Override
    public Page<NotificationDto> query(final Query query, final Pageable pageable) {
        return notificationQueryRepository.findAllByFilter(query.filter(), pageable)
                .map(Notification::toDto);
    }
}
