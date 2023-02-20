package com.mohaeng.notification.application.dto.type;

import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.type.ParticipantClubRoleChangedNotificationResponse;

import java.time.LocalDateTime;

public class ParticipantClubRoleChangedNotificationDto extends NotificationDto {

    private final Long clubId;
    private final Long clubRoleId;
    private final String clubRoleName;
    private final ClubRoleCategory clubRoleCategory;

    public ParticipantClubRoleChangedNotificationDto(final Long id,
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

    public Long clubId() {
        return clubId;
    }

    public Long clubRoleId() {
        return clubRoleId;
    }

    public String clubRoleName() {
        return clubRoleName;
    }

    public ClubRoleCategory clubRoleCategory() {
        return clubRoleCategory;
    }

    @Override
    public NotificationResponse toResponse() {
        return new ParticipantClubRoleChangedNotificationResponse(
                id(),
                createdAt(),
                isRead(),
                type(),
                clubId(),
                clubRoleId(),
                clubRoleName(),
                clubRoleCategory()
        );
    }
}
