package com.mohaeng.notification.domain.model.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.type.FillOutApplicationFormNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 가입 신청이 온 경우 생성되는 알림
 * 회장, 임원진들에게 가입 신청 요청에 대한 알림이 전송된다.
 */
@DiscriminatorValue(value = "FillOutApplicationFormNotification")
@Entity
public class FillOutApplicationFormNotification extends Notification {

    @Column(nullable = false)
    private Long clubId;  // 가입을 요청한 모임 ID

    @Column(nullable = false)
    private Long applicantId;  // 가입 신청자의 Member ID

    @Column(nullable = false)
    private Long applicationFormId;  // 가입 신청서 ID

    protected FillOutApplicationFormNotification() {
    }

    public FillOutApplicationFormNotification(final Receiver receiver,
                                              final Long clubId,
                                              final Long applicantId,
                                              final Long applicationFormId) {
        super(receiver);
        this.clubId = clubId;
        this.applicantId = applicantId;
        this.applicationFormId = applicationFormId;
    }

    public Long clubId() {
        return clubId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    @Override
    public NotificationDto toDto() {
        return new FillOutApplicationFormNotificationDto(
                id(),
                createdAt(),
                isRead(),
                getClass().getSimpleName(),
                clubId(),
                applicantId(),
                applicationFormId()
        );
    }
}