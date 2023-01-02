package com.mohaeng.application.club;

import com.mohaeng.domain.club.Club;
import com.mohaeng.domain.club.ClubRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MockClubRepository implements ClubRepository {

    private final Map<Long, Club> clubs = new HashMap<>();
    private long count = 1;

    @Override
    public Club save(final Club club) {
        clubs.put(count, club);
        LocalDateTime now = LocalDateTime.now();
        return new Club(count++, now, now, club.name(), club.description(), club.maxPeopleCount());
    }
}
