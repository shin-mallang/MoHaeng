package com.mohaeng.notification.application.dto.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.kind.ExpelParticipantNotificationResponse;

import java.time.LocalDateTime;

public class ExpelParticipantNotificationDto extends NotificationDto {

    private final Long clubId;  // 추방된 모임 ID

    public ExpelParticipantNotificationDto(final Long id,
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
        return new ExpelParticipantNotificationResponse(id(), createdAt(), isRead(), type(), clubId());
    }
}
