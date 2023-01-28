package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DeleteApplicationFormEvent")
public class DeleteApplicationFormEventHistory extends BaseEventHistory {

    private Long clubId;

    protected DeleteApplicationFormEventHistory() {
    }

    public DeleteApplicationFormEventHistory(final LocalDateTime eventDateTime, final Long clubId) {
        super(eventDateTime);
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }
}
