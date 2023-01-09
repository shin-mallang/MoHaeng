package com.mohaeng.alarm.domain.repository;

import com.mohaeng.alarm.domain.model.Alarm;
import com.mohaeng.alarm.domain.model.value.Receiver;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository {

    Alarm save(final Alarm alarm);

    Optional<Alarm> findById(final Long id);

    List<Alarm> findByReceiver(final Receiver receiver);

    List<Alarm> findAll();

    List<Alarm> saveAll(final List<Alarm> alarms);
}
