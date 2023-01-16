package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;

import java.time.LocalDateTime;

public class ApproveJoinClubEventHistory extends BaseEventHistory {

    private final Long clubId;

    protected ApproveJoinClubEventHistory() {
        this.clubId = null;
    }

    public ApproveJoinClubEventHistory(final LocalDateTime eventDateTime,
                                       final Long clubId) {
        super(eventDateTime);
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }
}
