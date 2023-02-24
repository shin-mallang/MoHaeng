package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.member.exception.MemberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

@Service
@Transactional
public class CreateClub implements CreateClubUseCase {

    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;

    public CreateClub(final MemberRepository memberRepository, final ClubRepository clubRepository) {
        this.memberRepository = memberRepository;
        this.clubRepository = clubRepository;
    }

    @Override
    public Long command(final Command command) {
        Member member = memberRepository.findById(command.memberId()).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        Club club = new Club(command.name(), command.description(), command.maxPeopleCount(), member);
        return clubRepository.save(club).id();
    }
}
