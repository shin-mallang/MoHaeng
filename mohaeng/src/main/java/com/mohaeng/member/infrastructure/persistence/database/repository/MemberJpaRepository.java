package com.mohaeng.member.infrastructure.persistence.database.repository;


import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberJpaRepository extends JpaRepository<Member, Long>, MemberRepository {

    @Override
    @Query("select m from Member m where m.id in :memberIds")
    List<Member> findByIdIn(@Param("memberIds") final List<Long> memberIds);
}
