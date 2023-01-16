package com.mohaeng.common.repositories;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MockClubRoleRepository implements ClubRoleRepository {

    private final Map<Long, ClubRole> store = new HashMap<>();
    private long sequence = 0L;

    public ClubRole save(final ClubRole clubRole) {
        ReflectionTestUtils.setField(clubRole, "id", ++sequence);
        store.put(clubRole.id(), clubRole);
        return clubRole;
    }

    @Override
    public List<ClubRole> saveAll(final List<ClubRole> defaultClubRoles) {
        return defaultClubRoles.stream()
                .map(this::save)
                .toList();
    }

    @Override
    public Optional<ClubRole> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<ClubRole> findDefaultGeneralRoleByClub(Club club) {
        return Optional.empty();
    }

    public List<ClubRole> findAll() {
        return store.values().stream().toList();
    }
}
