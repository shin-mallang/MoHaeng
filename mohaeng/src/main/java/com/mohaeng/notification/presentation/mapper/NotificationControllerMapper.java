package com.mohaeng.notification.presentation.mapper;

import com.mohaeng.notification.application.usecase.dto.NotificationDto;
import com.mohaeng.notification.presentation.QueryNotificationByIdController;

public class NotificationControllerMapper {

    public static QueryNotificationByIdController.NotificationResponse toResponseDto(final NotificationDto notificationDto) {
        return new QueryNotificationByIdController.NotificationResponse(
                notificationDto.createdAt(),
                notificationDto.title(),
                notificationDto.content(),
                notificationDto.alarmType(),
                notificationDto.isRead()
        );
    }
}
