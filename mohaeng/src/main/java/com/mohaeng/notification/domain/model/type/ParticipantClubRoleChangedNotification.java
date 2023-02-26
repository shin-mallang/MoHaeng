package com.mohaeng.notification.domain.model.type;

import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.type.ParticipantClubRoleChangedNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 참여자의 역할이 변경되었다는 알림
 */
@DiscriminatorValue(value = "ParticipantClubRoleChangedNotification")
@Entity
public class ParticipantClubRoleChangedNotification extends Notification {

    private Long clubId;
    private Long clubRoleId;
    private String clubRoleName;
    private ClubRoleCategory clubRoleCategory;

    protected ParticipantClubRoleChangedNotification() {
    }

    public ParticipantClubRoleChangedNotification(final Receiver receiver,
                                                  final Long clubId,
                                                  final Long clubRoleId,
                                                  final String clubRoleName,
                                                  final ClubRoleCategory clubRoleCategory) {
        super(receiver);
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
    public NotificationDto toDto() {
        return new ParticipantClubRoleChangedNotificationDto(
                id(),
                createdAt(),
                isRead(),
                getClass().getSimpleName(),
                clubId(),
                clubRoleId(),
                clubRoleName(),
                clubRoleCategory()
        );
    }
}
