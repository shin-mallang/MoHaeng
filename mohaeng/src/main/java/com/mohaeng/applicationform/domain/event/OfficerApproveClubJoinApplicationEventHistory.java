package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;

import java.time.LocalDateTime;

public class OfficerApproveClubJoinApplicationEventHistory extends BaseEventHistory {

    private Long managerId;  // 관리자 ID (Participant Id)
    private Long applicantId;  // 가입된 회원 ID (ParticipantId)
    private Long applicationFormId;  // 처리된 가입 신청서 ID

    protected OfficerApproveClubJoinApplicationEventHistory() {
    }

    public OfficerApproveClubJoinApplicationEventHistory(final LocalDateTime eventDateTime,
                                                         final Long managerId,
                                                         final Long applicantId,
                                                         final Long applicationFormId) {
        super(eventDateTime);
        this.managerId = managerId;
        this.applicantId = applicantId;
        this.applicationFormId = applicationFormId;
    }

    public Long managerId() {
        return managerId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }
}
