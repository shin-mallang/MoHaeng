package com.mohaeng.application.authentication.service;

import com.mohaeng.application.authentication.exception.IncorrectAuthenticationException;
import com.mohaeng.application.authentication.usecase.CreateTokenUseCase;
import com.mohaeng.application.authentication.usecase.LogInUseCase;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.domain.authentication.domain.Claims;
import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.domain.member.domain.MemberQuery;
import com.mohaeng.domain.member.domain.enums.PasswordMatchResult;
import com.mohaeng.infrastructure.persistence.database.service.member.exception.NotFoundMemberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LogIn implements LogInUseCase {

    public static final String MEMBER_ID_CLAIM = "memberId";

    private final MemberQuery memberQuery;
    private final CreateTokenUseCase createTokenUseCase;

    public LogIn(final MemberQuery memberQuery, final CreateTokenUseCase createTokenUseCase) {
        this.memberQuery = memberQuery;
        this.createTokenUseCase = createTokenUseCase;
    }

    @Override
    public AccessToken command(final Command command) {
        Member member = findByUsername(command.username());

        // 비밀번호 일치여부 확인
        matchPassword(command.password(), member);

        // AccessToken 생성
        return createToken(member);
    }

    /**
     * 아이디로 회원을 찾기
     */
    private Member findByUsername(final String username) {
        try {
            return memberQuery.findByUsername(username);
        } catch (NotFoundMemberException e) { // 회원이 없을 때
            throw new IncorrectAuthenticationException();
        }
    }

    /**
     * 비밀번호 일치 여부 확인
     */
    private static void matchPassword(final String password, final Member member) {
        PasswordMatchResult result = member.matchPassword(password);
        if (result == PasswordMatchResult.MISS_MATCH) { // 비밀번호가 일치하지 않을 때
            throw new IncorrectAuthenticationException();
        }
    }

    /**
     * 회원 정보를 가지고 토큰 생성하기
     */
    private AccessToken createToken(final Member member) {
        Claims claims = new Claims();
        claims.addClaims(MEMBER_ID_CLAIM, String.valueOf(member.id()));

        String accessToken = createTokenUseCase.command(
                new CreateTokenUseCase.Command(claims)
        );
        return new AccessToken(accessToken);
    }
}
