package com.mohaeng.notification.presentation.response.type;

import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.notification.presentation.response.NotificationResponse;

import java.time.LocalDateTime;

public class ParticipantClubRoleChangedNotificationResponse extends NotificationResponse {

    private final Long clubId;
    private final Long clubRoleId;
    private final String clubRoleName;
    private final ClubRoleCategory clubRoleCategory;

    public ParticipantClubRoleChangedNotificationResponse(final Long id,
                                                          final LocalDateTime createdAt,
                                                          final boolean isRead,
                                                          final String type,
                                                          final Long clubId,
                                                          final Long clubRoleId,
                                                          final String clubRoleName,
                                                          final ClubRoleCategory clubRoleCategory) {
        super(id, createdAt, isRead, type);
        this.clubId = clubId;
        this.clubRoleId = clubRoleId;
        this.clubRoleName = clubRoleName;
        this.clubRoleCategory = clubRoleCategory;
    }

    public Long getClubId() {
        return clubId;
    }

    public Long getClubRoleId() {
        return clubRoleId;
    }

    public String getClubRoleName() {
        return clubRoleName;
    }

    public ClubRoleCategory getClubRoleCategory() {
        return clubRoleCategory;
    }
}