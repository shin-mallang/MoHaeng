package com.mohaeng.infrastructure.persistence.database.repository.member;


import com.mohaeng.domain.member.model.Member;
import com.mohaeng.domain.member.repository.MemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long>, MemberRepository {
}
