package com.mohaeng.application.member.service;

import com.mohaeng.application.member.exception.DuplicateUsernameException;
import com.mohaeng.application.member.mapper.MemberApplicationMapper;
import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.infrastructure.persistence.database.service.member.MemberJpaCommand;
import com.mohaeng.infrastructure.persistence.database.service.member.MemberJpaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SignUp implements SignUpUseCase {

    private final MemberJpaCommand memberJpaCommand;
    private final MemberJpaQuery memberJpaQuery;

    public SignUp(final MemberJpaCommand memberJpaCommand, final MemberJpaQuery memberJpaQuery) {
        this.memberJpaCommand = memberJpaCommand;
        this.memberJpaQuery = memberJpaQuery;
    }

    @Override
    public void command(final Command command) {
        // 중복 아이디 검사
        checkDuplicateUsername(command.username());

        // TODO 비밀번호 암호화
        memberJpaCommand.save(MemberApplicationMapper.toPersistenceLayerDto(command));
    }

    /**
     * 중복 아이디 검사
     */
    private void checkDuplicateUsername(final String username) {
        if (memberJpaQuery.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
    }
}
