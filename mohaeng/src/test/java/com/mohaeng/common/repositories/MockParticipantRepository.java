package com.mohaeng.common.repositories;

import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockParticipantRepository implements ParticipantRepository {

    private final Map<Long, Participant> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Participant save(Participant participant) {
        ReflectionTestUtils.setField(participant, "id", ++sequence);
        store.put(participant.id(), participant);
        return participant;
    }

    public List<Participant> findAll() {
        return store.values().stream().toList();
    }
}
