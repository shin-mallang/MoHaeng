package com.mohaeng.domain.club.event.role;

import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.club.model.role.ClubRole;
import com.mohaeng.domain.config.event.BaseEvent;
import com.mohaeng.domain.config.event.BaseEventHistory;
import com.mohaeng.domain.member.model.Member;

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
