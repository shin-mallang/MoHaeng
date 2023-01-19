package com.mohaeng.notification.domain.model.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.kind.OfficerApproveApplicationNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.value.Receiver;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 가입 신청을 회장이 아닌 다른 임원진이 처리한 경우 회장에게 알림을 보내기 위함
 */
@DiscriminatorValue(value = "officer_processed_application_notification")
@Entity
public class OfficerApproveApplicationNotification extends Notification {

    @Column(nullable = false)
    private Long officerMemberId;  // 처리한 임원진 Member ID

    @Column(nullable = false)
    private Long officerParticipantId;  // 처리한 임원진 Participant ID (Member랑 Participant랑 뭘 줘야할 지 몰라서 둘 다 줌)

    @Column(nullable = false)
    private Long applicantMemberId;  // 가입된 회원의 Member ID

    @Column(nullable = false)
    private Long applicantParticipantId;  // 가입된 회원의 Participant ID

    protected OfficerApproveApplicationNotification() {
    }

    /**
     * @param receiver               수신자 : 회장의 Member ID
     * @param officerMemberId        처리한 임원진 Member ID
     * @param officerParticipantId   처리한 임원진 Participant ID
     * @param applicantMemberId      가입된 회원의 Member ID
     * @param applicantParticipantId 가입된 회원의 Participant ID
     */
    public OfficerApproveApplicationNotification(final Receiver receiver,
                                                 final Long officerMemberId,
                                                 final Long officerParticipantId,
                                                 final Long applicantMemberId,
                                                 final Long applicantParticipantId) {
        super(receiver);
        this.officerMemberId = officerMemberId;
        this.officerParticipantId = officerParticipantId;
        this.applicantMemberId = applicantMemberId;
        this.applicantParticipantId = applicantParticipantId;
    }

    public Long officerMemberId() {
        return officerMemberId;
    }

    public Long officerParticipantId() {
        return officerParticipantId;
    }

    public Long applicantMemberId() {
        return applicantMemberId;
    }

    public Long applicantParticipantId() {
        return applicantParticipantId;
    }

    @Override
    public NotificationDto toDto() {
        return new OfficerApproveApplicationNotificationDto(
                id(),
                createdAt(),
                isRead(),
                getClass().getSimpleName(),
                officerMemberId(),
                officerParticipantId(),
                applicantMemberId(),
                applicantParticipantId()
        );
    }
}
