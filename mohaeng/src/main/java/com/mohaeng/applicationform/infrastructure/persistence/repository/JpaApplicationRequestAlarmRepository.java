package com.mohaeng.applicationform.infrastructure.persistence.repository;

import com.mohaeng.applicationform.domain.model.ApplicationRequestAlarm;
import com.mohaeng.applicationform.domain.repository.ApplicationRequestAlarmRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaApplicationRequestAlarmRepository extends JpaRepository<ApplicationRequestAlarm, Long>, ApplicationRequestAlarmRepository {

    @Override
    default List<ApplicationRequestAlarm> saveAll(final List<ApplicationRequestAlarm> applicationRequestAlarms) {
        return saveAll((Iterable<ApplicationRequestAlarm>) applicationRequestAlarms);
    }
}
