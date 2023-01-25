package com.mohaeng.participant.domain.model;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.exception.ParticipantException;
import com.mohaeng.participant.exception.ParticipantExceptionType;
import jakarta.persistence.*;

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

    public Participant(final Member member) {
        this.member = member;
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

    public void joinClub(final Club club, final ClubRole clubRole) {
        this.club = club;
        this.clubRole = clubRole;
        club.participantCountUp();
    }

    /**
     * 관리자(회장, 임원)인지 확인
     */
    public boolean isManager() {
        return clubRole().isManagerRole();
    }

    /**
     * 모임에서 탈퇴
     */
    public void leaveFromClub() {
        // 모임에서 탈퇴할 수 있는지 확인한다.
        checkCanLeaveFromClub();

        club.participantCountDown();
    }

    /**
     * 모임에서 탈퇴할 수 있는지 확인한다.
     * (회장은 모임에서 탈퇴할 수 없다.)
     */
    private void checkCanLeaveFromClub() {
        if (this.isPresident()) {
            throw new ParticipantException(ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB);
        }
    }

    /**
     * 대상을 모임에서 추방시킨다.
     */
    public void expelFromClub(final Participant target) {
        // 추방시킬 권한 확인
        checkAuthorityExpel();

        target.club().participantCountDown();
    }

    /**
     * 회원을 추방시킬 권한이 있는지 확인한다.
     * (회장이 아니면 추방시킬 수 없다.)
     */
    private void checkAuthorityExpel() {
        if (!this.isPresident()) {
            throw new ParticipantException(ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT);
        }
    }

    /**
     * 회장인지 확인
     */
    private boolean isPresident() {
        return clubRole().isPresidentRole();
    }
}
