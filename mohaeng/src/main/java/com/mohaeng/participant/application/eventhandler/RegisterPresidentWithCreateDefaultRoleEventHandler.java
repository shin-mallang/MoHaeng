package com.mohaeng.participant.application.eventhandler;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.member.exception.MemberException;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NOT_FOUND_CLUB_ROLE;
import static com.mohaeng.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

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
                .orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));
        Member member = memberRepository.findById(event.memberId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        ClubRole clubRole = clubRoleRepository.findById(event.defaultPresidentRoleId())
                .orElseThrow(() -> new ClubRoleException(NOT_FOUND_CLUB_ROLE));

        Participant participant = new Participant(member);

        participant.joinClub(club, clubRole);

        participantRepository.save(participant);
        process(event);
    }
}
