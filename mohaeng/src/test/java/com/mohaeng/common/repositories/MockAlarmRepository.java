//package com.mohaeng.common.repositories;
//
//import com.mohaeng.alarm.domain.model.Alarm;
//import com.mohaeng.alarm.domain.model.value.Receiver;
//import com.mohaeng.alarm.domain.repository.AlarmRepository;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//public class MockAlarmRepository implements AlarmRepository {
//
//    private final Map<Long, Alarm> store = new HashMap<>();
//    private long sequence = 0L;
//
//    @Override
//    public Alarm save(final Alarm alarm) {
//        ReflectionTestUtils.setField(alarm, "id", ++sequence);
//        store.put(alarm.id(), alarm);
//        return alarm;
//    }
//
//    @Override
//    public Optional<Alarm> findById(Long id) {
//        return Optional.ofNullable(store.get(id));
//    }
//
//    @Override
//    public Optional<Alarm> findByIdAndReceiverId(Long id, Long receiverId) {
//        return Optional.empty();
//    }
//
//    @Override
//    public List<Alarm> findByReceiver(Receiver receiver) {
//        return store.values().stream().filter(it -> it.receiver().equals(receiver)).toList();
//    }
//
//    @Override
//    public List<Alarm> findAll() {
//        return store.values().stream().toList();
//    }
//
//    @Override
//    public List<Alarm> saveAll(List<Alarm> alarms) {
//        return alarms.stream().map(this::save).toList();
//    }
//}
