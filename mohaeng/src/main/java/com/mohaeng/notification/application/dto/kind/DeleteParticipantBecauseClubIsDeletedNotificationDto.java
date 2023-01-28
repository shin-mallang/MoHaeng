package com.mohaeng.notification.application.dto.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.kind.DeleteParticipantBecauseClubIsDeletedNotificationResponse;

import java.time.LocalDateTime;

public class DeleteParticipantBecauseClubIsDeletedNotificationDto extends NotificationDto {

    private final String clubName;  // 제거된 모임 이름
    private final String clubDescription;  // 제거된 모임의 설명

    public DeleteParticipantBecauseClubIsDeletedNotificationDto(final Long id,
                                                                final LocalDateTime createdAt,
                                                                final boolean isRead,
                                                                final String type,
                                                                final String clubName,
                                                                final String clubDescription) {
        super(id, createdAt, isRead, type);
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }

    @Override
    public NotificationResponse toResponse() {
        return new DeleteParticipantBecauseClubIsDeletedNotificationResponse(id(), createdAt(), isRead(), type(), clubName, clubDescription);
    }
}