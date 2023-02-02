package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OfficerApproveApplicationFormEvent")
public class OfficerApproveApplicationFormEventHistory extends BaseEventHistory {

    private Long managerMemberId;  // 관리자 ID (Member Id)
    private Long managerParticipantId;  // 관리자 ID (Participant Id)
    private Long applicantMemberId;  // 가입된 회원 ID (Member Id)
    private Long applicantParticipantId;  // 가입된 회원 ID (Participant Id)
    private Long applicationFormId;  // 처리된 가입 신청서 ID

    protected OfficerApproveApplicationFormEventHistory() {
    }

    public OfficerApproveApplicationFormEventHistory(final Long managerMemberId,
                                                     final Long managerParticipantId,
                                                     final Long applicantMemberId,
                                                     final Long applicantParticipantId,
                                                     final Long applicationFormId) {
        this.managerMemberId = managerMemberId;
        this.managerParticipantId = managerParticipantId;
        this.applicantMemberId = applicantMemberId;
        this.applicantParticipantId = applicantParticipantId;
        this.applicationFormId = applicationFormId;
    }

    public Long managerMemberId() {
        return managerMemberId;
    }

    public Long managerParticipantId() {
        return managerParticipantId;
    }

    public Long applicantMemberId() {
        return applicantMemberId;
    }

    public Long applicantParticipantId() {
        return applicantParticipantId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }
}
