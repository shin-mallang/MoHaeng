package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

/**
 * 모임의 참여자가 모두 제거된 경우 발행되는 이벤트
 * <p>
 * 참여자들에게 모임이 제거되었다는 알림 발행
 */
public class DeleteClubParticipantEvent extends NotificationEvent {

    private final String clubName;
    private final String clubDescription;

    public DeleteClubParticipantEvent(final Object source,
                                      final List<Long> receiverIds,
                                      final String clubName,
                                      final String clubDescription) {
        super(source, receiverIds);
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }

    public String clubName() {
        return clubName;
    }

    public String clubDescription() {
        return clubDescription;
    }

    @Override
    public BaseEventHistory history() {
        return new DeleteClubParticipantEventHistory(eventDateTime, clubName, clubDescription);
    }

    @Override
    public String toString() {
        return "DeleteClubParticipantEvent{" +
                "clubName='" + clubName + '\'' +
                ", clubDescription='" + clubDescription + '\'' +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}
