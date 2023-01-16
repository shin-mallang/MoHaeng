package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.common.event.BaseEventHistory;

import java.util.List;

public class ApproveJoinClubEvent extends AlarmEvent {

    private final Long clubId;

    /**
     * @param receiverId 가입된 회원 ID
     */
    public ApproveJoinClubEvent(final Object source,
                                final Long receiverId,
                                final Long clubId) {
        super(source, List.of(receiverId));
        this.clubId = clubId;
    }

    @Override
    public String toString() {
        return "ApproveJoinClubEvent{" +
                "clubId=" + clubId +
                '}';
    }

    @Override
    public BaseEventHistory history() {
        return new ApproveJoinClubEventHistory(eventDateTime, clubId);
    }

    public Long clubId() {
        return clubId;
    }
}
