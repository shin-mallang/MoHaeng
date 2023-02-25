package com.mohaeng.notification.domain.model.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.type.OfficerRejectApplicationNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 임원이 가입 신청서를 거절했을 때, 회장에게 전송될 알림
 */
@DiscriminatorValue(value = "OfficerRejectApplicationNotification")
@Entity
public class OfficerRejectApplicationNotification extends Notification {

    private Long officerMemberId;  // 처리한 임원진 Member ID

    private Long officerParticipantId;  // 처리한 임원진 Participant ID (Member랑 Participant랑 뭘 줘야할 지 몰라서 둘 다 줌)

    private Long applicantMemberId;  // 거절된 회원의 Member ID

    protected OfficerRejectApplicationNotification() {
    }

    /**
     * @param receiver             수신자 : 회장의 Member ID
     * @param officerMemberId      처리한 임원진 Member ID
     * @param officerParticipantId 처리한 임원진 Participant ID
     * @param applicantMemberId    거절된 회원의 Member ID
     */
    public OfficerRejectApplicationNotification(final Receiver receiver,
                                                final Long officerMemberId,
                                                final Long officerParticipantId,
                                                final Long applicantMemberId) {
        super(receiver);
        this.officerMemberId = officerMemberId;
        this.officerParticipantId = officerParticipantId;
        this.applicantMemberId = applicantMemberId;
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

    @Override
    public NotificationDto toDto() {
        return new OfficerRejectApplicationNotificationDto(
                id(),
                createdAt(),
                isRead(),
                getClass().getSimpleName(),
                officerMemberId(),
                officerParticipantId(),
                applicantMemberId()
        );
    }
}