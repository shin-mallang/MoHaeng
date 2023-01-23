package com.mohaeng.clubrole.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("CreateDefaultRoleEvent")
public class CreateDefaultRoleHistory extends BaseEventHistory {

    private final Long memberId;
    private final Long clubId;
    private final Long defaultPresidentRoleId;  // 기본 회장 역할 ID

    protected CreateDefaultRoleHistory() {
        this.memberId = null;
        this.clubId = null;
        this.defaultPresidentRoleId = null;
    }

    public CreateDefaultRoleHistory(final LocalDateTime eventDateTime,
                                    final Long memberId,
                                    final Long clubId,
                                    final Long defaultPresidentRoleId) {
        super(eventDateTime);
        this.memberId = memberId;
        this.clubId = clubId;
        this.defaultPresidentRoleId = defaultPresidentRoleId;
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
