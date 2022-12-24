package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import com.mohaeng.infrastructure.persistence.database.service.member.exception.NotFoundMemberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberQuery {

    private final MemberRepository memberRepository;

    public MemberQuery(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean existsByUsername(final String username) {
        return memberRepository.existsByUsername(username);
    }

    public MemberJpaEntity findByUsername(final String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);
    }
}
