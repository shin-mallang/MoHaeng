package com.mohaeng.common.repositories;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

public class MockClubRepository implements ClubRepository {

    private final Map<Long, Club> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Club save(final Club club) {
        ReflectionTestUtils.setField(club, "id", ++sequence);
        store.put(club.id(), club);
        return club;
    }
}
