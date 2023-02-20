package com.mohaeng.club.club.domain.event.history;

import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Entity
@DiscriminatorValue("ParticipantClubRoleChangedEvent")
public class ParticipantClubRoleChangedEventHistory extends BaseEventHistory {

    private Long clubId;
    private Long clubRoleId;
    private String clubRoleName;
    @Enumerated(STRING)
    private ClubRoleCategory clubRoleCategory;

    protected ParticipantClubRoleChangedEventHistory() {
    }

    public ParticipantClubRoleChangedEventHistory(final LocalDateTime eventDateTime,
                                                  final Long clubId,
                                                  final Long clubRoleId,
                                                  final String clubRoleName,
                                                  final ClubRoleCategory category) {
        super(eventDateTime);
        this.clubId = clubId;
        this.clubRoleId = clubRoleId;
        this.clubRoleName = clubRoleName;
        this.clubRoleCategory = category;
    }

    public Long clubId() {
        return clubId;
    }

    public Long clubRoleId() {
        return clubRoleId;
    }

    public String clubRoleName() {
        return clubRoleName;
    }

    public ClubRoleCategory clubRoleCategory() {
        return clubRoleCategory;
    }
}