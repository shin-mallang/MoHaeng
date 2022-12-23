package com.mohaeng.domain.member.service;

import com.mohaeng.common.member.dto.CreateMemberDto;
import com.mohaeng.domain.member.exception.DuplicateUsernameException;
import com.mohaeng.domain.member.usecase.SignUpUseCase;
import com.mohaeng.infrastructure.persistence.database.service.member.MemberCommand;
import com.mohaeng.infrastructure.persistence.database.service.member.MemberQuery;
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
        memberCommand.save(new CreateMemberDto(
                command.username(),
                command.password(),
                command.name(),
                command.age(),
                command.gender()
        ));
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
