package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

/**
 * 모임의 참여자가 모두 제거된 경우 발행되는 알림
 */
public class DeleteClubParticipantEvent extends NotificationEvent {

    private final Long clubId;

    public DeleteClubParticipantEvent(final Object source,
                                      final List<Long> receiverIds,
                                      final Long clubId) {
        super(source, receiverIds);
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }

    @Override
    public String toString() {
        return "DeleteClubParticipantEvent{" +
                "clubId=" + clubId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }

    @Override
    public BaseEventHistory history() {
        return new DeleteClubParticipantEventHistory(eventDateTime, clubId);
    }
}
