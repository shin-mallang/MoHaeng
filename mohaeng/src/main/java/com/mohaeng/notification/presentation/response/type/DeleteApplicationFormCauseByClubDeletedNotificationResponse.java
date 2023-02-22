package com.mohaeng.notification.presentation.response.type;

import com.mohaeng.notification.presentation.response.NotificationResponse;

import java.time.LocalDateTime;

public class DeleteApplicationFormCauseByClubDeletedNotificationResponse extends NotificationResponse {

    private final String clubName;  // 제거된 모임 이름
    private final String clubDescription;  // 제거된 모임의 설명

    public DeleteApplicationFormCauseByClubDeletedNotificationResponse(final Long id,
                                                                       final LocalDateTime createdAt,
                                                                       final boolean isRead,
                                                                       final String type,
                                                                       final String clubName,
                                                                       final String clubDescription) {
        super(id, createdAt, isRead, type);
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }

    public String getClubName() {
        return clubName;
    }

    public String getClubDescription() {
        return clubDescription;
    }
}