package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberCommand {

    private final MemberRepository memberRepository;

    public MemberCommand(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
}
