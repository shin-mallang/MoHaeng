package com.mohaeng.club.club.domain.event;

import com.mohaeng.club.club.domain.event.history.DeleteClubEventHistory;
import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

public class DeleteClubEvent extends NotificationEvent {

    private final Long clubId;
    private final String clubName;
    private final String clubDescription;

    public DeleteClubEvent(final Object source,
                           final List<Long> receiverIds,  // 제거된 모임에 가입되어있던 참여자의 MemberId
                           final Long clubId,
                           final String clubName,
                           final String clubDescription) {
        super(source, receiverIds);
        this.clubId = clubId;
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }

    public Long clubId() {
        return clubId;
    }

    public String clubName() {
        return clubName;
    }

    public String clubDescription() {
        return clubDescription;
    }

    @Override
    public BaseEventHistory history() {
        return new DeleteClubEventHistory(eventDateTime, clubId);
    }

    @Override
    public String toString() {
        return "DeleteClubEvent{" +
                "clubId=" + clubId +
                ", clubName='" + clubName + '\'' +
                ", clubDescription='" + clubDescription + '\'' +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}