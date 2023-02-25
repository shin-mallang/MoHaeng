package com.mohaeng.notification.application.service.query;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryNotificationByIdUseCase;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.notification.exception.NotificationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;

@Service
@Transactional
public class QueryNotificationById implements QueryNotificationByIdUseCase {

    private final NotificationRepository notificationRepository;

    public QueryNotificationById(final NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationDto query(final Query query) {
        Notification notification = notificationRepository.findById(query.id()).orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));
        notification.read();
        return notification.toDto();
    }
}
