package com.mohaeng.member.application.service;

import com.mohaeng.member.exception.DuplicateUsernameException;
import com.mohaeng.member.application.mapper.MemberApplicationMapper;
import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SignUp implements SignUpUseCase {

    private final MemberRepository memberRepository;

    public SignUp(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void command(final Command command) {
        // 중복 아이디 검사
        checkDuplicateUsername(command.username());

        // TODO 비밀번호 암호화
        memberRepository.save(MemberApplicationMapper.toDomainEntity(command));
    }

    /**
     * 중복 아이디 검사
     */
    private void checkDuplicateUsername(final String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
    }
}
