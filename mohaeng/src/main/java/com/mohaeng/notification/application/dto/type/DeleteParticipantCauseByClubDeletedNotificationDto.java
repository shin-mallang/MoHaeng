package com.mohaeng.notification.application.dto.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.type.DeleteParticipantCauseByClubDeletedNotificationResponse;

import java.time.LocalDateTime;

public class DeleteParticipantCauseByClubDeletedNotificationDto extends NotificationDto {

    private final String clubName;  // 제거된 모임 이름
    private final String clubDescription;  // 제거된 모임의 설명

    public DeleteParticipantCauseByClubDeletedNotificationDto(final Long id,
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
        return new DeleteParticipantCauseByClubDeletedNotificationResponse(id(), createdAt(), isRead(), type(), clubName, clubDescription);
    }
}