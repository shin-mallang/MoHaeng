package com.mohaeng.club.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("CreateClubEvent")
public class CreateClubEventHistory extends BaseEventHistory {

    private Long memberId;
    private Long clubId;

    protected CreateClubEventHistory() {
    }

    public CreateClubEventHistory(final LocalDateTime eventDateTime,
                                  final Long memberId,
                                  final Long clubId) {
        super(eventDateTime);
        this.memberId = memberId;
        this.clubId = clubId;
    }

    public Long memberId() {
        return memberId;
    }

    public Long clubId() {
        return clubId;
    }
}
