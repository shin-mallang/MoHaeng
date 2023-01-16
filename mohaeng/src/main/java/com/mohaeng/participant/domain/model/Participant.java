package com.mohaeng.participant.domain.model;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.applicationform.exception.ApplicationFormExceptionType;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
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
    private boolean isManager() {
        return clubRole().isManagerRole();
    }

    /**
     * 주어진 가입 신청서를 승인한다
     */
    public Participant acceptJoinClub(final ApplicationForm applicationForm, final ClubRole defaultGeneralRole) {
        // 승인 권한 확인
        checkAuthorityToProcessApplication();

        // 모임에 가입시키기
        Participant applicant = new Participant(applicationForm.applicant());
        applicant.joinClub(applicationForm.target(), defaultGeneralRole);
        return applicant;
    }

    /**
     * 가입 신청을 처리할 권한이 있는지 확인
     *
     * @throws ApplicationFormException 관리자가 아닌 경우
     */
    private void checkAuthorityToProcessApplication() throws ApplicationFormException {
        if (!isManager()) {
            throw new ApplicationFormException(ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM);
        }
    }
}
