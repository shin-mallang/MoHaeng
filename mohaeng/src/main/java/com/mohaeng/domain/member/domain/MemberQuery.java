package com.mohaeng.domain.member.domain;

public interface MemberQuery {

    boolean existsByUsername(final String username);

    Member findByUsername(final String username);
}
