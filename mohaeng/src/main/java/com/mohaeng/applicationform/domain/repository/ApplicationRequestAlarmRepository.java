package com.mohaeng.applicationform.domain.repository;

import com.mohaeng.applicationform.domain.model.ApplicationRequestAlarm;

import java.util.List;

public interface ApplicationRequestAlarmRepository {
    List<ApplicationRequestAlarm> saveAll(final List<ApplicationRequestAlarm> applicationRequestAlarms);
}
