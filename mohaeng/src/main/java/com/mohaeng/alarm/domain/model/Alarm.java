package com.mohaeng.alarm.domain.model;

import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.alarm.domain.model.value.Receiver;
import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.*;

/**
 * 알림이 필요하면 해당 클래스를 상속받는다.
 */
@Entity
@Table(name = "alarm")
public class Alarm extends BaseEntity {

    @Embedded
    private Receiver receiver;  // 수신자

    @Embedded
    private AlarmMessage alarmMessage;  // 알람 내용

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private boolean isRead;  // 알람 읽음 여부

    protected Alarm() {
    }

    public Alarm(final Receiver receiver,
                 final AlarmMessage alarmMessage,
                 final AlarmType alarmType) {
        this.receiver = receiver;
        this.alarmMessage = alarmMessage;
        this.alarmType = alarmType;
        this.isRead = false;
    }

    public static Alarm of(final Receiver receiver, final AlarmMessage alarmMessage, final AlarmType alarmType) {
        return new Alarm(receiver, alarmMessage, alarmType);
    }

    public Receiver receiver() {
        return receiver;
    }

    public AlarmMessage alarmMessage() {
        return alarmMessage;
    }

    public AlarmType alarmType() {
        return alarmType;
    }

    public boolean isRead() {
        return isRead;
    }

    public void read() {
        this.isRead = true;
    }
}
