package com.mohaeng.club.domain.event;

import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.event.BaseEventHistory;

public class CreateClubEvent extends BaseEvent {

    private final Long memberId;
    private final Long clubId;

    public CreateClubEvent(final Object source, final Long memberId, final Long clubId) {
        super(source);
        this.memberId = memberId;
        this.clubId = clubId;
    }

    @Override
    public BaseEventHistory history() {
        return new CreateClubEventHistory(eventDateTime, memberId, clubId);
    }

    public Long memberId() {
        return memberId;
    }

    public Long clubId() {
        return clubId;
    }
}
