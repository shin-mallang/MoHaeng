package com.mohaeng.notification.presentation.response.type;

import com.mohaeng.notification.presentation.response.NotificationResponse;

import java.time.LocalDateTime;

public class ExpelParticipantNotificationResponse extends NotificationResponse {

    private final Long clubId;  // 추방된 모임 ID

    public ExpelParticipantNotificationResponse(final Long id,
                                                final LocalDateTime createdAt,
                                                final boolean isRead,
                                                final String type,
                                                final Long clubId) {
        super(id, createdAt, isRead, type);
        this.clubId = clubId;
    }

    public Long getClubId() {
        return clubId;
    }
}