package com.mohaeng.club.domain.event;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.member.domain.model.Member;

public class CreateClubEvent extends BaseEvent {

    private final Member member;
    private final Club club;

    public CreateClubEvent(final Object source, final Member member, final Club club) {
        super(source);
        this.member = member;
        this.club = club;
    }

    @Override
    public BaseEventHistory history() {
        return new CreateClubEventHistory(eventDateTime, member.id(), club.id());
    }

    public Member member() {
        return member;
    }

    public Club club() {
        return club;
    }
}
