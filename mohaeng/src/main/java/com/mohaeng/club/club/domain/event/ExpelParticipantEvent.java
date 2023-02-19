package com.mohaeng.club.club.domain.event;

import com.mohaeng.club.club.domain.event.history.ExpelParticipantEventHistory;
import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

/**
 * 회원 추방 시 발행
 */
public class ExpelParticipantEvent extends NotificationEvent {

    private final Long clubId;

    public ExpelParticipantEvent(final Object source,
                                 final Long expelledMemberId,
                                 final Long clubId) {
        super(source, expelledMemberId);
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
        return new ExpelParticipantEventHistory(eventDateTime, clubId);
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