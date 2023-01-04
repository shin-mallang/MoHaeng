package com.mohaeng.domain.club.event.club;

import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.config.event.BaseEvent;
import com.mohaeng.domain.config.event.BaseEventHistory;

public class CreateClubEvent extends BaseEvent {

    private final Long memberId;
    private final Club club;

    public CreateClubEvent(final Object source, final Long memberId, final Club club) {
        super(source);
        this.memberId = memberId;
        this.club = club;
    }

    @Override
    public BaseEventHistory history() {
        return new CreateClubEventHistory(eventDateTime, memberId, club.id());
    }

    public Long memberId() {
        return memberId;
    }

    public Club club() {
        return club;
    }
}
