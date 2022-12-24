package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.infrastructure.persistence.database.service.member.dto.CreateMemberDto;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import com.mohaeng.infrastructure.persistence.database.service.member.mapper.MemberPersistenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberCommand {

    private final MemberRepository memberRepository;

    public MemberCommand(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void save(final CreateMemberDto createMemberDto) {
        MemberJpaEntity memberJpaEntity = MemberPersistenceMapper.toJpaEntity(createMemberDto);
        memberRepository.save(memberJpaEntity);
    }
}
