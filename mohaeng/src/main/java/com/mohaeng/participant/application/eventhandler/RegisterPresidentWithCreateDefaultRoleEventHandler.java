package com.mohaeng.participant.application.eventhandler;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.NotFoundClubException;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.NotFoundClubRoleException;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.member.exception.NotFoundMemberException;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RegisterPresidentWithCreateDefaultRoleEventHandler extends EventHandler<CreateDefaultRoleEvent> {

    private final ParticipantRepository participantRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubRoleRepository clubRoleRepository;

    public RegisterPresidentWithCreateDefaultRoleEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                              final ParticipantRepository participantRepository,
                                                              final MemberRepository memberRepository,
                                                              final ClubRepository clubRepository,
                                                              final ClubRoleRepository clubRoleRepository) {
        super(eventHistoryRepository);
        this.participantRepository = participantRepository;
        this.memberRepository = memberRepository;
        this.clubRepository = clubRepository;
        this.clubRoleRepository = clubRoleRepository;
    }

    @Transactional
    @EventListener
    @Override
    public void handle(final CreateDefaultRoleEvent event) {
        Club club = clubRepository.findById(event.clubId())
                .orElseThrow(() -> new NotFoundClubException(event.clubId()));
        Member member = memberRepository.findById(event.memberId())
                .orElseThrow(() -> new NotFoundMemberException(event.memberId()));
        ClubRole clubRole = clubRoleRepository.findById(event.defaultPresidentRoleId())
                .orElseThrow(() -> new NotFoundClubRoleException(event.defaultPresidentRoleId()));

        Participant participant = new Participant(member);

        participant.joinClub(club, clubRole);

        participantRepository.save(participant);
        process(event);
    }
}
