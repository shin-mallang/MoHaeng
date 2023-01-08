package com.mohaeng.applicationform.domain.model;

import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED_APPLICATION_FORM;

/**
 * 가입 신청서
 */
@Where(clause = "processed = false")  // soft delete 형식으로, 처리된 가입 신청서는 조회하지 않음
@SQLDelete(sql = "UPDATE application_form SET processed = true WHERE id = ?")
@Entity
@Table(name = "application_form")
public class ApplicationForm extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Member applicant;  // 신청자(회원) id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Club target;  // 가입을 희망하는 모임 id

    private boolean processed = Boolean.FALSE;  // 처리된 경우 실제 delete 대신 processed = true로 처리

    protected ApplicationForm() {
    }

    private ApplicationForm(final Member applicant, final Club target) {
        this.applicant = applicant;
        this.target = target;
    }

    public static ApplicationForm create(final Member member, final Club club) {
        return new ApplicationForm(member, club);
    }

    public Member applicant() {
        return applicant;
    }

    public Club target() {
        return target;
    }

    public boolean processed() {
        return processed;
    }

    /**
     * 가입 신청서 처리
     *
     * @throws ApplicationFormException (ALREADY_PROCESSED_APPLICATION_FORM) 이미 처리된 신청서를 또다시 처리하려는 경우
     */
    public void process() throws ApplicationFormException {
        if (this.processed) {
            throw new ApplicationFormException(ALREADY_PROCESSED_APPLICATION_FORM);
        }
        this.processed = true;
    }
}
