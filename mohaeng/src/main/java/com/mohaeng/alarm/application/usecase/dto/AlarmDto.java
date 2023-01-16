package com.mohaeng.alarm.application.usecase.dto;

import java.time.LocalDateTime;

public record AlarmDto(
        LocalDateTime createdAt,  // 알람 발송일
        String title,
        String content,
        String alarmType,
        boolean isRead  // 알람 읽음 여부
) {
}