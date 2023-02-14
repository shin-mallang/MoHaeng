package com.mohaeng.notification.application.dto.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.type.ApplicationRejectedNotificationResponse;

import java.time.LocalDateTime;

public class ApplicationRejectedNotificationDto extends NotificationDto {

    private final Long clubId;  // 가입을 요청한 모임 ID

    public ApplicationRejectedNotificationDto(final Long id,
                                              final LocalDateTime createdAt,
                                              final boolean isRead,
                                              final String type,
                                              final Long clubId) {
        super(id, createdAt, isRead, type);
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }

    @Override
    public NotificationResponse toResponse() {
        return new ApplicationRejectedNotificationResponse(id(), createdAt(), isRead(), type(), clubId());
    }
}