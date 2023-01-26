package com.mohaeng.notification.domain.model.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.kind.OfficerRejectApplicationNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.value.Receiver;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 가입 신청을 회장이 아닌 다른 임원진이 처리한 경우 회장에게 알림을 보내기 위함
 */
@DiscriminatorValue(value = "OfficerRejectApplicationNotification")
@Entity
public class OfficerRejectApplicationNotification extends Notification {

    @Column(nullable = false)
    private Long officerMemberId;  // 처리한 임원진 Member ID

    @Column(nullable = false)
    private Long officerParticipantId;  // 처리한 임원진 Participant ID (Member랑 Participant랑 뭘 줘야할 지 몰라서 둘 다 줌)

    @Column(nullable = false)
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
