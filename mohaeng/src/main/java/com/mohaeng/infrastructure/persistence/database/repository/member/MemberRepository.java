package com.mohaeng.infrastructure.persistence.database.repository.member;


import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberJpaEntity, Long> {

    boolean existsByUsername(final String username);

    Optional<MemberJpaEntity> findByUsername(final String username);
}
