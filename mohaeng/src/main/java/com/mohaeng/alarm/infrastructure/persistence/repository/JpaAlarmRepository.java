package com.mohaeng.alarm.infrastructure.persistence.repository;

import com.mohaeng.alarm.domain.model.Alarm;
import com.mohaeng.alarm.domain.repository.AlarmRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaAlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepository {

    @Override
    default List<Alarm> saveAll(final List<Alarm> alarms) {
        return saveAll((Iterable<Alarm>) alarms);
    }
}
