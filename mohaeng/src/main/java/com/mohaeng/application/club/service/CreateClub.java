package com.mohaeng.application.club.service;

import com.mohaeng.application.club.mapper.ClubApplicationMapper;
import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.application.member.exception.NotFoundMemberException;
import com.mohaeng.common.event.Event;
import com.mohaeng.domain.club.event.club.CreateClubEvent;
import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.club.repository.club.ClubRepository;
import com.mohaeng.domain.member.model.Member;
import com.mohaeng.domain.member.repository.MemberRepository;
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
        Event.publish(new CreateClubEvent(this, member, club));

        return club.id();
    }
}
