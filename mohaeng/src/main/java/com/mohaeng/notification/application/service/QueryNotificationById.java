package com.mohaeng.notification.application.service;

import com.mohaeng.notification.application.mapper.NotificationApplicationMapper;
import com.mohaeng.notification.application.usecase.QueryNotificationByIdUseCase;
import com.mohaeng.notification.application.usecase.dto.NotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.notification.exception.NotificationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.notification.exception.NotificationExceptionType.NOT_FOUND_APPLICATION_FORM;

@Service
@Transactional(readOnly = true)
public class QueryNotificationById implements QueryNotificationByIdUseCase {

    private final NotificationRepository notificationRepository;

    public QueryNotificationById(final NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationDto query(final Query query) {
        Notification notification = notificationRepository.findByIdAndReceiverId(query.alarmId(), query.memberId()).orElseThrow(() -> new NotificationException(NOT_FOUND_APPLICATION_FORM));

        notification.read();  // 알림 읽음 처리

        return NotificationApplicationMapper.toPresentationDto(notification);
    }
}
