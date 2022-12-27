package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.domain.member.domain.MemberCommand;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import com.mohaeng.infrastructure.persistence.database.service.member.mapper.MemberPersistenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberJpaCommand implements MemberCommand {

    private final MemberRepository memberRepository;

    public MemberJpaCommand(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void save(final Member member) {
        MemberJpaEntity memberJpaEntity = MemberPersistenceMapper.toJpaEntity(member);
        memberRepository.save(memberJpaEntity);
    }
}
