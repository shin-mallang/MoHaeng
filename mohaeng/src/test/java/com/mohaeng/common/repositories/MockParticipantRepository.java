package com.mohaeng.common.repositories;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.PRESIDENT;

public class MockParticipantRepository implements ParticipantRepository {

    private final Map<Long, Participant> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Participant save(Participant participant) {
        ReflectionTestUtils.setField(participant, "id", ++sequence);
        store.put(participant.id(), participant);
        return participant;
    }

    @Override
    public boolean existsByMemberAndClub(Member member, Club club) {
        return store.values().stream().anyMatch(it -> it.member().id().equals(member.id()) && it.club().id().equals(club.id()));
    }

    /**
     * "select p from Participant p join fetch p.member where p.clubRole.clubRoleCategory = 'PRESIDENT' or p.clubRole.clubRoleCategory = 'OFFICER'"
     */
    @Override
    public List<Participant> findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(Long clubId) {
        return store.values().stream().
                filter(it -> it.clubRole().clubRoleCategory().equals(PRESIDENT)
                        || it.clubRole().clubRoleCategory().equals(OFFICER))
                .toList();
    }

    public List<Participant> findAll() {
        return store.values().stream().toList();
    }
}
