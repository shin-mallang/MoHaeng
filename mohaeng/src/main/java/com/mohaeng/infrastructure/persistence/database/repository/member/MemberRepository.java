package com.mohaeng.infrastructure.persistence.database.repository.member;


import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberJpaEntity, Long> {

    boolean existsByUsername(final String username);
}
