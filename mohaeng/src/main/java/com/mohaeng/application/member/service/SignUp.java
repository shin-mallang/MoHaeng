package com.mohaeng.application.member.service;

import com.mohaeng.application.member.exception.DuplicateUsernameException;
import com.mohaeng.application.member.mapper.MemberApplicationMapper;
import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.domain.member.domain.MemberCommand;
import com.mohaeng.domain.member.domain.MemberQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SignUp implements SignUpUseCase {

    private final MemberCommand memberCommand;
    private final MemberQuery memberQuery;

    public SignUp(final MemberCommand memberCommand, final MemberQuery memberQuery) {
        this.memberCommand = memberCommand;
        this.memberQuery = memberQuery;
    }

    @Override
    public void command(final Command command) {
        // 중복 아이디 검사
        checkDuplicateUsername(command.username());

        // TODO 비밀번호 암호화
        memberCommand.save(MemberApplicationMapper.toDomainEntity(command));
    }

    /**
     * 중복 아이디 검사
     */
    private void checkDuplicateUsername(final String username) {
        if (memberQuery.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
    }
}
