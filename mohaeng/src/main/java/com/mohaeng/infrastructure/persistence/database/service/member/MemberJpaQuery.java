package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.domain.member.domain.MemberQuery;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import com.mohaeng.infrastructure.persistence.database.service.member.exception.NotFoundMemberException;
import com.mohaeng.infrastructure.persistence.database.service.member.mapper.MemberPersistenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberJpaQuery implements MemberQuery {

    private final MemberRepository memberRepository;

    public MemberJpaQuery(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean existsByUsername(final String username) {
        return memberRepository.existsByUsername(username);
    }

    @Override
    public Member findByUsername(final String username) {
        MemberJpaEntity memberJpaEntity = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);
        return MemberPersistenceMapper.toDomainEntity(memberJpaEntity);
    }
}
