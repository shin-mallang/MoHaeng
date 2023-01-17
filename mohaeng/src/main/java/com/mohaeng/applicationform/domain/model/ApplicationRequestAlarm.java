package com.mohaeng.applicationform.domain.model;

import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.Entity;

/**
 * 가입 신청 요청에 대한 알림
 */
@Entity
public class ApplicationRequestAlarm extends BaseEntity {

    private Long applicantId;  // 가입 신청한 Member ID

    private Long clubId;  // 대상 모임 ID

    private Long applicationFormId;  // 해당 알림에 해당하는 가입 신청서 ID

    private Long receiverId;  // 해당 알림을 받은 Member ID

    protected ApplicationRequestAlarm() {
    }

    public ApplicationRequestAlarm(final Long applicantId,
                                   final Long clubId,
                                   final Long applicationFormId,
                                   final Long receiverId) {
        this.applicantId = applicantId;
        this.clubId = clubId;
        this.applicationFormId = applicationFormId;
        this.receiverId = receiverId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long clubId() {
        return clubId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    public Long receiverId() {
        return receiverId;
    }
}
