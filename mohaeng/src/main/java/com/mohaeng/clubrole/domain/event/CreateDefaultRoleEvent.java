package com.mohaeng.clubrole.domain.event;

import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.event.BaseEventHistory;

public class CreateDefaultRoleEvent extends BaseEvent {

    private final Long memberId;
    private final Long clubId;
    private final Long defaultPresidentRoleId;  // 기본 회장 역할

    public CreateDefaultRoleEvent(final Object source,
                                  final Long memberId,
                                  final Long clubId,
                                  final Long defaultPresidentRoleId) {
        super(source);
        this.memberId = memberId;
        this.clubId = clubId;
        this.defaultPresidentRoleId = defaultPresidentRoleId;
    }

    @Override
    public BaseEventHistory history() {
        return new CreateDefaultRoleHistory(eventDateTime, memberId, clubId, defaultPresidentRoleId);
    }

    public Long memberId() {
        return memberId;
    }

    public Long clubId() {
        return clubId;
    }

    public Long defaultPresidentRoleId() {
        return defaultPresidentRoleId;
    }
}