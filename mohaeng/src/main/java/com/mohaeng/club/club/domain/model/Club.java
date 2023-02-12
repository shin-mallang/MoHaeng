package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubExceptionType;
import com.mohaeng.club.participant.model.Participant;
import com.mohaeng.club.participant.model.Participants;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    private String name;
    private String description;
    private int maxParticipantCount;
    private int currentParticipantCount;  // 현재 가입한 인원 수

    @Embedded
    private ClubRoles clubRoles;

    @Embedded
    private Participants participants;

    protected Club() {
    }

    public Club(final String name,
                final String description,
                final int maxParticipantCount,
                final Member member) {
        this.name = name;
        this.description = description;
        this.maxParticipantCount = maxParticipantCount;
        this.clubRoles = ClubRoles.defaultRoles(this);
        this.participants = Participants.initWithPresident(new Participant(member, this, findDefaultRoleByCategory(ClubRoleCategory.PRESIDENT)));
        participantCountUp();
    }

    private void participantCountUp() {
        if (currentParticipantCount >= maxParticipantCount) {
            throw new ClubException(ClubExceptionType.CLUB_IS_FULL);
        }
        currentParticipantCount++;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int maxParticipantCount() {
        return maxParticipantCount;
    }

    public int currentParticipantCount() {
        return currentParticipantCount;
    }

    public ClubRoles clubRoles() {
        return clubRoles;
    }

    public Participants participants() {
        return participants;
    }

    public ClubRole findDefaultRoleByCategory(final ClubRoleCategory category) {
        return clubRoles().findDefaultRoleByCategory(category);
    }
}