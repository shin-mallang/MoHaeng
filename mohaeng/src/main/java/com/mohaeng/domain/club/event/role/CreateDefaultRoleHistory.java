package com.mohaeng.domain.club.event.role;

import com.mohaeng.domain.config.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("CreateDefaultRoleEvent")
public class CreateDefaultRoleHistory extends BaseEventHistory {

    private Long memberId;
    private Long clubId;
    private Long defaultPresidentRoleId;  // 기본 회장 역할 ID

    protected CreateDefaultRoleHistory() {
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
