package com.mohaeng.club.domain.event;

import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.event.BaseEventHistory;

public class DeleteClubEvent extends BaseEvent {

    private final Long clubId;

    public DeleteClubEvent(final Object source, final Long clubId) {
        super(source);
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }

    @Override
    public BaseEventHistory history() {
        return new DeleteClubEventHistory(eventDateTime, clubId);
    }
}
