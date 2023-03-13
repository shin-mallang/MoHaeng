package com.mohaeng.club.applicationform.domain.model;

import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION;
import static java.lang.Boolean.FALSE;

@Entity
@Table(name = "application_form")
public class ApplicationForm extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Member applicant;  // 신청자(회원)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;  // 가입을 희망하는 모임

    private boolean processed = FALSE;

    protected ApplicationForm() {
    }

    private ApplicationForm(final Club club, final Member applicant) {
        this.applicant = applicant;
        this.club = club;
    }

    public static ApplicationForm create(final Club club, final Member applicant) {
        return new ApplicationForm(club, applicant);
    }

    public Member applicant() {
        return applicant;
    }

    public Club club() {
        return club;
    }

    public boolean processed() {
        return this.processed;
    }

    public void approve(final Participant manager) {
        validateProcess(manager);
        process();
        club.registerParticipant(applicant);
    }

    public void reject(final Participant manager) {
        validateProcess(manager);
        process();
    }

    private void validateProcess(final Participant manager) {
        if (!manager.isManager() || !club.contains(manager)) {
            throw new ApplicationFormException(NO_AUTHORITY_PROCESS_APPLICATION);
        }
    }

    public void process() {
        if (this.processed) {
            throw new ApplicationFormException(ALREADY_PROCESSED);
        }
        this.processed = true;
    }
}
