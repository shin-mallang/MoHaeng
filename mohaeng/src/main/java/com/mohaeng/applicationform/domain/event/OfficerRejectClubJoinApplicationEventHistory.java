package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OfficerRejectClubJoinApplicationEvent")
public class OfficerRejectClubJoinApplicationEventHistory extends BaseEventHistory {

    private final Long managerMemberId;  // 관리자 ID (Member Id)
    private final Long managerParticipantId;  // 관리자 ID (Participant Id)
    private final Long applicantMemberId;  // 거절된 회원 ID (Member Id)
    private final Long applicationFormId;  // 처리된 가입 신청서 ID

    protected OfficerRejectClubJoinApplicationEventHistory() {
        this.managerMemberId = null;
        this.managerParticipantId = null;
        this.applicantMemberId = null;
        this.applicationFormId = null;
    }

    public OfficerRejectClubJoinApplicationEventHistory(final Long managerMemberId,
                                                        final Long managerParticipantId,
                                                        final Long applicantMemberId,
                                                        final Long applicationFormId) {
        this.managerMemberId = managerMemberId;
        this.managerParticipantId = managerParticipantId;
        this.applicantMemberId = applicantMemberId;
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

    public Long applicationFormId() {
        return applicationFormId;
    }
}
