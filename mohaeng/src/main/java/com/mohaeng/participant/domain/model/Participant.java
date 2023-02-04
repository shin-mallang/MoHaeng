package com.mohaeng.participant.domain.model;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.clubrole.exception.ClubRoleExceptionType;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.exception.ParticipantException;
import jakarta.persistence.*;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM;
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
     * 역할을 변경한다.
     */
    public void changeRole(final ClubRole clubRole) {
        this.clubRole = clubRole;
    }

    /**
     * 상대방의 역할을 변경한다.
     */
    public void changeTargetRole(final Participant target, final ClubRole clubRole) {
        // 회장 역할이 아닌지 확인한다.
        checkChangeRoleCategoryIsNotPresident(clubRole);

        // 대상의 역할을 수정할 권한이 있는지 확인한다.
        checkAuthorityToChangeTargetRole(target, clubRole);

        // 역할 변경
        target.changeRole(clubRole);
    }

    /**
     * 바꾸려는 역할이 회장 역할이 아닌지 확인한다.
     */
    private void checkChangeRoleCategoryIsNotPresident(final ClubRole clubRole) {
        if (clubRole.clubRoleCategory() == PRESIDENT) {
            throw new ClubRoleException(ClubRoleExceptionType.CAN_NOT_CHANGED_TO_PRESIDENT_ROLE);
        }
    }

    /**
     * 상대방의 역할을 수정할 수 있는지 확인한다.
     * <p>
     * 상대방이 나보다 계급이 낮아야 하며, 나와 동일하거나 낮은 계급으로만 변경 가능하다.
     * (예를 들어 회장은, 모든 회원을 임원 혹은 일반 회원으로 변경할 수 있고,
     * 임원은 일반 회원만을 임원으로 변경할 수 있다.)
     *
     * @param target   역할을 변경할 대상
     * @param clubRole 변경하고 싶은 역할
     */
    private void checkAuthorityToChangeTargetRole(final Participant target, final ClubRole clubRole) {
        // 일반 회원인 경우 역할을 변경할 수 없음
        if (isGeneral()) {
            throw new ClubRoleException(NO_AUTHORITY_CHANGE_TARGET_ROLE);
        }

        // 대상이 나보다 계급이 높거나 같은 경우 예외 발생
        if (!this.clubRole().isPowerfulThan(target.clubRole())) {
            throw new ClubRoleException(NO_AUTHORITY_CHANGE_TARGET_ROLE);
        }

        // 다른 모임의 계급인 경우
        if (!this.clubRole().club().id().equals(clubRole.club().id())) {
            throw new ClubRoleException(CAN_NOT_CHANGE_TO_OTHER_CLUB_ROLE);
        }

        // 바꾸려는 계급이 나보다 높은 계급인 경우, 회장으로 바꾸는 경우 이미 걸려졌으므로
        // 해당 상황은 발생하지 않는다.
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
     * 모임에서 탈퇴한다.
     */
    public void leaveFromClub() {
        // 모임에서 탈퇴할 수 있는지 확인한다.
        checkCanLeaveFromClub();

        club.participantCountDown();

        this.club = null;
        this.clubRole = null;
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
        // 추방시키려는 사람이 회장이 아닌 경우 예외
        checkAuthorityExpel();

        target.club().participantCountDown();

        target.club = null;
        target.clubRole = null;
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

    /**
     * 모임의 역할을 새로 생성한다.
     */
    public ClubRole createClubRole(final String name,
                                   final ClubRoleCategory clubRoleCategory) {
        // 생성하려는 회원이 회장이나 임원이 아닌 경우 예외 발생
        checkAuthorityCreateClubRole();

        // 회장을 생성하는 경우 예외 발생
        checkCreateCategoryIsNotPresident(clubRoleCategory);

        return new ClubRole(name, clubRoleCategory, club(), false);
    }

    /**
     * 모임의 역할을 생성할 권할을 확인한다.
     */
    private void checkAuthorityCreateClubRole() {
        if (this.isGeneral()) {
            throw new ClubRoleException(NO_AUTHORITY_CREATE_ROLE);
        }
    }

    /**
     * 회장 역할을 생성하려는 경우 예외를 발생시킨다.
     */
    private void checkCreateCategoryIsNotPresident(final ClubRoleCategory clubRoleCategory) {
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
     * 주어진 역할을 제거한다.
     */
    public void deleteClubRole(final ClubRole clubRole) {
        // 회장 혹은 임원이 아닌 경우 예외
        checkAuthorityDeleteClubRole();

        clubRole.makeNotDefault();
    }

    private void checkAuthorityDeleteClubRole() {
        if (isGeneral()) {
            throw new ClubRoleException(NO_AUTHORITY_DELETE_ROLE);
        }
    }

    /**
     * 기본 역할을 변경한다.
     *
     * @param defaultRoleCandidate 기본 역할로 만들고 싶은 역할
     * @param existingDefaultRole  기존의 기본 역할
     */
    public void changeDefaultRole(final ClubRole defaultRoleCandidate, ClubRole existingDefaultRole) {
        // 회장 혹은 임원이 아닌 경우 예외
        checkAuthorityChangeDefaultRole();

        // 두 역할의 카테고리가 일치하는지 확인
        checkCategoryIsMatchBetweenExistingDefaultRoleAndCandidate(defaultRoleCandidate, existingDefaultRole);

        defaultRoleCandidate.makeDefault();
        existingDefaultRole.makeNotDefault();
    }

    /**
     * 기본 역할 변경 권한 확인
     */
    private void checkAuthorityChangeDefaultRole() {
        if (isGeneral()) {
            throw new ClubRoleException(NO_AUTHORITY_CHANGE_DEFAULT_ROLE);
        }
    }

    /**
     * 두 역할의 카테고리가 일치하는지 확인한다.
     *
     * @param defaultRoleCandidate 기본 역할로 변경될 역할
     * @param existingDefaultRole  기존의 기본 역할
     */
    private void checkCategoryIsMatchBetweenExistingDefaultRoleAndCandidate(final ClubRole defaultRoleCandidate, final ClubRole existingDefaultRole) {
        if (defaultRoleCandidate.clubRoleCategory() != existingDefaultRole.clubRoleCategory()) {
            throw new ClubRoleException(MISMATCH_EXISTING_DEFAULT_ROLE_AND_CANDIDATE);
        }
    }

    /**
     * 가입 신청서를 승인한 후, Participant를 생성하여 반환한다.
     */
    public Participant approveApplicationForm(final ApplicationForm applicationForm, final ClubRole defaultGeneralRole) {
        // 회장 혹은 임원이 아닌 경우 예외
        checkAuthorityToProcessApplication();

        // 모임에 가입시키기
        Participant applicant = new Participant(applicationForm.applicant());
        applicant.joinClub(applicationForm.target(), defaultGeneralRole);

        // 가입 신청서 처리
        applicationForm.process();

        return applicant;
    }

    /**
     * 가입 신청서를 거절한다.
     */
    public void rejectApplicationForm(final ApplicationForm applicationForm) {
        // 회장 혹은 임원이 아닌 경우 예외
        checkAuthorityToProcessApplication();

        // 가입 신청서 처리
        applicationForm.process();
    }

    /**
     * 가입 신청서를 처리할 권한을 확인한다.
     */
    private void checkAuthorityToProcessApplication() {
        if (isGeneral()) {
            throw new ApplicationFormException(NO_AUTHORITY_PROCESS_APPLICATION_FORM);
        }
    }

    /**
     * 회장인지 확인
     */
    private boolean isPresident() {
        return clubRole().isPresidentRole();
    }

    /**
     * 일반 회원인지 여부
     */
    private boolean isGeneral() {
        return clubRole.isGeneralRole();
    }
}
