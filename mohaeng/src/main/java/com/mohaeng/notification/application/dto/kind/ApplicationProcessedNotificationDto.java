package com.mohaeng.notification.application.dto.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.kind.ApplicationProcessedNotificationResponse;

import java.time.LocalDateTime;

public class ApplicationProcessedNotificationDto extends NotificationDto {

    private Long clubId;  // 가입을 요청한 모임 ID
    private boolean isApproved;  // 수락된 경우 true, 거절된 경우 false

    public ApplicationProcessedNotificationDto(final Long id,
                                               final LocalDateTime createdAt,
                                               final boolean isRead,
                                               final String type,
                                               final Long clubId,
                                               final boolean isApproved) {
        super(id, createdAt, isRead, type);
        this.clubId = clubId;
        this.isApproved = isApproved;
    }

    public Long clubId() {
        return clubId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    @Override
    public NotificationResponse toResponse() {
        return new ApplicationProcessedNotificationResponse(id(), createdAt(), isRead(), type(), clubId(), isApproved());
    }
}
