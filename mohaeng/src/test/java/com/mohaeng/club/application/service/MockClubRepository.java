package com.mohaeng.club.application.service;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.mohaeng.common.fixtures.ClubFixture.club;

public class MockClubRepository implements ClubRepository {

    private final Map<Long, Club> clubs = new HashMap<>();
    private long count = 1;

    @Override
    public Club save(final Club club) {
        clubs.put(count, club);

        ReflectionTestUtils.setField(club, "id", count++);
        return club;
    }
}
