package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.domain.event.ExpelParticipantEvent;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.common.event.Events;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.*;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT;

@Entity
@Table(name = "participant")
public class Participant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;  // 가입된 club

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_role_id")
    private ClubRole clubRole;  // 모임에서의 역할

    protected Participant() {
    }

    public Participant(final Member member, final Club club, final ClubRole clubRole) {
        this.member = member;
        this.club = club;
        this.clubRole = clubRole;
    }

    public Member member() {
        return member;
    }

    public Club club() {
        return club;
    }

    public ClubRole clubRole() {
        return clubRole;
    }

    public boolean isManager() {
        return this.clubRole().clubRoleCategory() != GENERAL;
    }

    public boolean isPresident() {
        return this.clubRole().clubRoleCategory() == PRESIDENT;
    }

    /**
     * 회원 추방 기능
     */
    public void expel(final Participant expelTarget) {
        validateExpelAuthority(expelTarget);
        club.deleteParticipant(expelTarget);
        Events.raise(new ExpelParticipantEvent(this, expelTarget.member.id(), club().id()));
    }

    private void validateExpelAuthority(final Participant expelTarget) {
        if (!expelTarget.club().equals(this.club())) {
            throw new ParticipantException(NO_AUTHORITY_EXPEL_PARTICIPANT);
        }
        if (!this.isPresident()) {
            throw new ParticipantException(NO_AUTHORITY_EXPEL_PARTICIPANT);
        }
    }
}