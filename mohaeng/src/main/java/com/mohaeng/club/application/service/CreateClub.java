package com.mohaeng.club.application.service;

import com.mohaeng.club.application.mapper.ClubApplicationMapper;
import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.common.event.Events;
import com.mohaeng.member.application.exception.NotFoundMemberException;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CreateClub implements CreateClubUseCase {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    public CreateClub(final ClubRepository clubRepository,
                      final MemberRepository memberRepository) {
        this.clubRepository = clubRepository;
        this.memberRepository = memberRepository;
    }


    @Override
    public Long command(final Command command) {
        Member member = memberRepository.findById(command.memberId()).orElseThrow(NotFoundMemberException::new);

        Club club = clubRepository.save(ClubApplicationMapper.toDomainEntity(command));

        // CreateClubEvent 를 받으면 -> 모임 기본 역할 등록(등록 이후 모임 생성한 사람을 회장으로 만들기)
        Events.raise(new CreateClubEvent(this, member.id(), club.id()));

        return club.id();
    }
}
