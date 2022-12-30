package com.mohaeng.infrastructure.persistence.database.repository.member;


import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.domain.member.domain.MemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long>, MemberRepository {
}
