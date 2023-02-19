package com.mohaeng.club.club.domain.event.history;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ExpelParticipantEvent")
public class ExpelParticipantEventHistory extends BaseEventHistory {

    private Long clubId;

    protected ExpelParticipantEventHistory() {
    }

    public ExpelParticipantEventHistory(final LocalDateTime eventDateTime, final Long clubId) {
        super(eventDateTime);
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }
}