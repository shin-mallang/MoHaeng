package com.mohaeng.application.authentication.service;

import com.mohaeng.application.authentication.exception.IncorrectAuthenticationException;
import com.mohaeng.application.authentication.usecase.CreateTokenUseCase;
import com.mohaeng.application.authentication.usecase.LogInUseCase;
import com.mohaeng.domain.authentication.model.AccessToken;
import com.mohaeng.domain.authentication.model.Claims;
import com.mohaeng.domain.member.model.Member;
import com.mohaeng.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class LogIn implements LogInUseCase {

    public static final String MEMBER_ID_CLAIM = "memberId";

    private final MemberRepository memberRepository;
    private final CreateTokenUseCase createTokenUseCase;

    public LogIn(final MemberRepository memberRepository, final CreateTokenUseCase createTokenUseCase) {
        this.memberRepository = memberRepository;
        this.createTokenUseCase = createTokenUseCase;
    }

    @Override
    public AccessToken command(final Command command) {
        Member member = findByUsername(command.username());

        // 비밀번호 일치여부 확인
        member.login(command.username(), command.password());

        // AccessToken 생성
        return createToken(member);
    }

    /**
     * 아이디로 회원을 찾기
     */
    private Member findByUsername(final String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(IncorrectAuthenticationException::new);
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
