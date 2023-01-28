package com.mohaeng.notification.application.dto.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.kind.DeleteApplicationFormBecauseClubIsDeletedNotificationResponse;

import java.time.LocalDateTime;

public class DeleteApplicationFormBecauseClubIsDeletedNotificationDto extends NotificationDto {

    private final String clubName;  // 제거된 모임 이름
    private final String clubDescription;  // 제거된 모임의 설명

    public DeleteApplicationFormBecauseClubIsDeletedNotificationDto(final Long id,
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
        return new DeleteApplicationFormBecauseClubIsDeletedNotificationResponse(id(), createdAt(), isRead(), type(), clubName, clubDescription);
    }
}
