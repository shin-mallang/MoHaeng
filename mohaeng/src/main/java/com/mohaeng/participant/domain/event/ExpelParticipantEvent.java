package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

/**
 * 회원 추방 시 발행
 */
public class ExpelParticipantEvent extends NotificationEvent {

    private final Long clubId;

    public ExpelParticipantEvent(final Object source,
                                 final Long expelledMemberId,
                                 final Long clubId) {
        super(source, List.of(expelledMemberId));
        this.clubId = clubId;
    }

    public Long receiverId() {
        return receiverIds.get(0);
    }

    public Long clubId() {
        return clubId;
    }

    @Override
    public BaseEventHistory history() {
        return new ExpelParticipantEventHistory(clubId);
    }

    @Override
    public String toString() {
        return "ExpelParticipantEvent{" +
                "clubId=" + clubId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}
