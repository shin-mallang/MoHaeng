package com.mohaeng.clubrole.domain.event;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.member.domain.model.Member;

public class CreateDefaultRoleEvent extends BaseEvent {

    private final Member member;
    private final Club club;
    private final ClubRole defaultPresidentRole;  // 기본 회장 역할

    public CreateDefaultRoleEvent(final Object source,
                                  final Member member,
                                  final Club club,
                                  final ClubRole defaultPresidentRole) {
        super(source);
        this.member = member;
        this.club = club;
        this.defaultPresidentRole = defaultPresidentRole;
    }

    @Override
    public BaseEventHistory history() {
        return new CreateDefaultRoleHistory(eventDateTime, member.id(), club.id(), defaultPresidentRole.id());
    }

    public Member member() {
        return member;
    }

    public Club club() {
        return club;
    }

    public ClubRole defaultPresidentRole() {
        return defaultPresidentRole;
    }
}
