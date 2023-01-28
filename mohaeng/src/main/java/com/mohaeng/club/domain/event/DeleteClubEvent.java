package com.mohaeng.club.domain.event;

import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.event.BaseEventHistory;

public class DeleteClubEvent extends BaseEvent {

    private final Long clubId;
    private final String clubName;
    private final String clubDescription;

    public DeleteClubEvent(final Object source,
                           final Long clubId,
                           final String clubName,
                           final String clubDescription) {
        super(source);
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
}
