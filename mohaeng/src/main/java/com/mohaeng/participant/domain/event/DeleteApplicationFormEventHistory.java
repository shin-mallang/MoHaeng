package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DeleteApplicationFormEvent")
public class DeleteApplicationFormEventHistory extends BaseEventHistory {

    private String clubName;
    private String clubDescription;

    protected DeleteApplicationFormEventHistory() {
    }

    public DeleteApplicationFormEventHistory(final LocalDateTime eventDateTime,
                                             final String clubName,
                                             final String clubDescription) {
        super(eventDateTime);
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }

    public String clubName() {
        return clubName;
    }

    public String clubDescription() {
        return clubDescription;
    }
}
