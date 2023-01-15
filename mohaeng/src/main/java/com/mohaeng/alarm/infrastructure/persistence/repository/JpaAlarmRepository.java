package com.mohaeng.alarm.infrastructure.persistence.repository;

import com.mohaeng.alarm.domain.model.Alarm;
import com.mohaeng.alarm.domain.repository.AlarmRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaAlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepository {

    @Override
    default List<Alarm> saveAll(final List<Alarm> alarms) {
        return saveAll((Iterable<Alarm>) alarms);
    }

    @Override
    @Query("select a from Alarm a where a.id = :alarmId and a.receiver.receiver.id = :receiverId")
    Optional<Alarm> findByIdAndReceiverId(@Param("alarmId") final Long alarmId, @Param("receiverId") final Long receiverId);
}
