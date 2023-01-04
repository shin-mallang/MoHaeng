package com.mohaeng.domain.member.repository;

import com.mohaeng.domain.member.model.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(final Member member);

    boolean existsByUsername(final String username);

    Optional<Member> findByUsername(final String username);
}
