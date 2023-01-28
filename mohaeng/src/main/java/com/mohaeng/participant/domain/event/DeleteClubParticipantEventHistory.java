package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

/**
 * 모임의 참여자가 모두 제거된 경우 발행되는 알림
 */
@Entity
@DiscriminatorValue("ClubParticipantDeleteEvent")
public class DeleteClubParticipantEventHistory extends BaseEventHistory {

    private String clubName;
    private String clubDescription;

    protected DeleteClubParticipantEventHistory() {
    }

    public DeleteClubParticipantEventHistory(final LocalDateTime eventDateTime,
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
