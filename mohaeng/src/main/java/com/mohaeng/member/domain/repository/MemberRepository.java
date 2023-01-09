package com.mohaeng.member.domain.repository;

import com.mohaeng.member.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(final Member member);

    boolean existsByUsername(final String username);

    Optional<Member> findByUsername(final String username);

    Optional<Member> findById(final Long id);

    List<Member> findByIdIn(final List<Long> memberIds);
}
