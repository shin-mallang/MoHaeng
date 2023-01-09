package com.mohaeng.common.repositories;

import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MockMemberRepository implements MemberRepository {

    private final Map<Long, Member> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Member save(final Member member) {
        ReflectionTestUtils.setField(member, "id", ++sequence);
        store.put(member.id(), member);
        return member;
    }

    @Override
    public boolean existsByUsername(final String username) {
        return store.values().stream()
                .anyMatch(it -> it.username().equals(username));
    }

    @Override
    public Optional<Member> findByUsername(final String username) {
        return store.values().stream()
                .filter(it -> it.username().equals(username))
                .findAny();
    }

    @Override
    public Optional<Member> findById(final Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Member> findByIdIn(List<Long> memberIds) {
        return store.values().stream().filter(it -> memberIds.contains(it.id()))
                .toList();
    }
}
