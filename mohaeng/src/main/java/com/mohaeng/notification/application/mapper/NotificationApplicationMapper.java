package com.mohaeng.notification.application.mapper;

import com.mohaeng.notification.application.usecase.dto.NotificationDto;
import com.mohaeng.notification.domain.model.Notification;

public class NotificationApplicationMapper {

    public static NotificationDto toPresentationDto(final Notification notification) {
        return new NotificationDto(
                notification.createdAt(),
                notification.alarmMessage().title(),
                notification.alarmMessage().content(),
                notification.alarmType().name(),
                notification.isRead()
        );
    }
}
