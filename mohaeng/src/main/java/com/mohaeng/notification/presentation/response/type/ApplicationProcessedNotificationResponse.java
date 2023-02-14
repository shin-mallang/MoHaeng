package com.mohaeng.notification.presentation.response.type;

import com.mohaeng.notification.presentation.response.NotificationResponse;

import java.time.LocalDateTime;

public class ApplicationProcessedNotificationResponse extends NotificationResponse {

    private final Long clubId;  // 가입을 요청한 모임 ID
    private final boolean isApproved;

    public ApplicationProcessedNotificationResponse(final Long id,
                                                    final LocalDateTime createdAt,
                                                    final boolean isRead,
                                                    final String type,
                                                    final Long clubId,
                                                    final boolean isApproved) {
        super(id, createdAt, isRead, type);
        this.clubId = clubId;
        this.isApproved = isApproved;
    }

    public Long getClubId() {
        return clubId;
    }

    public boolean isApproved() {
        return isApproved;
    }
}