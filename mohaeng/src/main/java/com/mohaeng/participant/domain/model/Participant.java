package com.mohaeng.participant.domain.model;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.exception.ParticipantException;
import jakarta.persistence.*;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.*;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT;
import static com.mohaeng.participant.exception.ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB;

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

    /**
     * 모임에 가입한다.
     */
    public void joinClub(final Club club, final ClubRole clubRole) {
        club.participantCountUp();
        this.club = club;
        this.clubRole = clubRole;
    }

    /**
     * 모임에서 탈퇴
     */
    public void leaveFromClub() {
        // 모임에서 탈퇴할 수 있는지 확인한다.
        checkCanLeaveFromClub();

        club.participantCountDown();

        this.club = null;
        this.clubRole = null;
        this.member = null;
    }

    /**
     * 모임에서 탈퇴할 수 있는지 확인한다.
     * (회장은 모임에서 탈퇴할 수 없다.)
     */
    private void checkCanLeaveFromClub() {
        if (this.isPresident()) {
            throw new ParticipantException(PRESIDENT_CAN_NOT_LEAVE_CLUB);
        }
    }

    /**
     * 대상을 모임에서 추방시킨다.
     */
    public void expelFromClub(final Participant target) {
        // 추방시킬 권한 확인
        checkAuthorityExpel();

        target.club().participantCountDown();

        target.club = null;
        target.clubRole = null;
        target.member = null;
    }

    /**
     * 회원을 추방시킬 권한이 있는지 확인한다.
     * (회장이 아니면 추방시킬 수 없다.)
     */
    private void checkAuthorityExpel() {
        if (!this.isPresident()) {
            throw new ParticipantException(NO_AUTHORITY_EXPEL_PARTICIPANT);
        }
    }

    public ClubRole createClubRole(final String name,
                                   final ClubRoleCategory clubRoleCategory) {
        // 생성하려는 회원이 회장이나 임원이 아닌 경우 예외 발생
        checkAuthorityCreateClubRole();

        // 회장을 생성하는 경우 예외 발생
        checkCategoryIsNotPresident(clubRoleCategory);

        return new ClubRole(name, clubRoleCategory, club(), false);
    }

    private void checkAuthorityCreateClubRole() {
        if (this.isGeneral()) {
            throw new ClubRoleException(NO_AUTHORITY_CREATE_ROLE);
        }
    }

    private void checkCategoryIsNotPresident(final ClubRoleCategory clubRoleCategory) {
        if (clubRoleCategory == PRESIDENT) {
            throw new ClubRoleException(CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE);
        }
    }

    /**
     * 역할 이름을 변경한다.
     *
     * @param clubRole 이름을 바꿀 역할
     * @param roleName 바꾸고싶은 이름
     */
    public void changeClubRoleName(final ClubRole clubRole, final String roleName) {
        // 이름을 변경하려는 회원이 회장이나 임원이 아닌 경우 예외 발생
        checkAuthorityChangeClubRoleName();

        // 이름 변경
        clubRole.changeName(roleName);
    }

    private void checkAuthorityChangeClubRoleName() {
        if (this.isGeneral()) {
            throw new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }
    }

    /**
     * 회장인지 확인
     */
    private boolean isPresident() {
        return clubRole().isPresidentRole();
    }

    /**
     * 관리자(회장, 임원)인지 확인
     */
    public boolean isManager() {
        return clubRole().isManagerRole();
    }

    /**
     * 일반 회원인지 여부
     */
    private boolean isGeneral() {
        return clubRole.isGeneralRole();
    }
}
