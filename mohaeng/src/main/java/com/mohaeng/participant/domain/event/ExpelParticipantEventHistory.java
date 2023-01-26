package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 회원 추방 시 발행
 */
@Entity
@DiscriminatorValue("ExpelParticipantEvent")
public class ExpelParticipantEventHistory extends BaseEventHistory {

    private Long clubId;

    protected ExpelParticipantEventHistory() {
        this.clubId = null;
    }

    public ExpelParticipantEventHistory(final Long clubId) {
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }
}
