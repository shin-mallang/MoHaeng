package com.mohaeng.domain.club.event.club;

import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.config.event.BaseEvent;
import com.mohaeng.domain.config.event.BaseEventHistory;
import com.mohaeng.domain.member.model.Member;

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
