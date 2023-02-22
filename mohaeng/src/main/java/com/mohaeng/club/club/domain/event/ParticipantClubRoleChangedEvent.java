package com.mohaeng.club.club.domain.event;

import com.mohaeng.club.club.domain.event.history.ParticipantClubRoleChangedEventHistory;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

/**
 * 역할이 변경되었을 경우 알림
 */
public class ParticipantClubRoleChangedEvent extends NotificationEvent {

    private final Long clubId;
    private final Long clubRoleId;
    private final String clubRoleName;
    private final ClubRoleCategory clubRoleCategory;

    public ParticipantClubRoleChangedEvent(final Object source,
                                           final Long receiverId,
                                           final Long clubId,
                                           final Long clubRoleId,
                                           final String clubRoleName,
                                           final ClubRoleCategory category) {
        super(source, receiverId);
        this.clubId = clubId;
        this.clubRoleId = clubRoleId;
        this.clubRoleName = clubRoleName;
        this.clubRoleCategory = category;
    }

    public Long receiverId() {
        return receiverIds.get(0);
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
    public BaseEventHistory history() {
        return new ParticipantClubRoleChangedEventHistory(eventDateTime, clubId, clubRoleId, clubRoleName, clubRoleCategory);
    }

    @Override
    public String toString() {
        return "ParticipantClubRoleChangedEvent{" +
                "clubId=" + clubId +
                ", clubRoleId=" + clubRoleId +
                ", clubRoleName='" + clubRoleName + '\'' +
                ", clubRoleCategory=" + clubRoleCategory +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}
