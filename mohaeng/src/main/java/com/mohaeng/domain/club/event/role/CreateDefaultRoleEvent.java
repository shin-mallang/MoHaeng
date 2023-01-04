package com.mohaeng.domain.club.event.role;

import com.mohaeng.domain.config.event.BaseEvent;
import com.mohaeng.domain.config.event.BaseEventHistory;

public class CreateDefaultRoleEvent extends BaseEvent {

    private final Long memberId;
    private final Long clubId;
    private final Long defaultPresidentRoleId;  // 기본 회장 역할 ID

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
        return new CreateDefaultRoleHistory();
    }
}
