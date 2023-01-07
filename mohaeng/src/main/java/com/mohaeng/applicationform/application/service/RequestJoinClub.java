package com.mohaeng.applicationform.application.service;

import com.mohaeng.applicationform.application.usecase.RequestJoinClubUseCase;
import com.mohaeng.applicationform.domain.event.RequestJoinClubEvent;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.AlreadyJoinedMemberException;
import com.mohaeng.applicationform.exception.AlreadyRequestJoinClubException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.NotFoundClubException;
import com.mohaeng.common.event.Events;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.member.exception.NotFoundMemberException;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RequestJoinClub implements RequestJoinClubUseCase {

    private final ApplicationFormRepository applicationFormRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final ParticipantRepository participantRepository;

    public RequestJoinClub(final ApplicationFormRepository applicationFormRepository,
                           final MemberRepository memberRepository,
                           final ClubRepository clubRepository,
                           final ParticipantRepository participantRepository) {
        this.applicationFormRepository = applicationFormRepository;
        this.memberRepository = memberRepository;
        this.clubRepository = clubRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public Long command(final Command command) {
        Member member = memberRepository.findById(command.applicantId())
                .orElseThrow(() -> new NotFoundMemberException(command.applicantId()));
        Club club = clubRepository.findById(command.targetClubId())
                .orElseThrow(() -> new NotFoundClubException(command.targetClubId()));

        // 이미 가입된 회원이거나, 이미 가입 신청을 한 회원이라면 오류
        validate(member, club);

        ApplicationForm applicationForm = ApplicationForm.create(member, club);
        applicationFormRepository.save(applicationForm);

        // 모임 가입 신청 요청 이벤트 발행 -> 회장과 임원진에게 알림 보내기
        Events.raise(new RequestJoinClubEvent(this, member.id(), club.id(), applicationForm.id()));

        return applicationForm.id();
    }

    /**
     * 이미 가입된 모임이거나, 이미 가입 신청을 한 회원인지 확인
     *
     * @throws AlreadyJoinedMemberException    이미 회원이 모임에 가입되어 있는 경우
     * @throws AlreadyRequestJoinClubException 이미 가입 신청한 경우
     */
    private void validate(final Member member, final Club club) throws AlreadyJoinedMemberException, AlreadyRequestJoinClubException {
        // 이미 가입되어있는지 확인
        validateAlreadyJoinedMember(member, club);

        // 이미 가입 신청 하였는지 확인
        validateAlreadyRequested(member, club);
    }

    /**
     * 이미 회원이 모임에 가입되어 있는지 검증
     *
     * @throws AlreadyJoinedMemberException 이미 회원이 모임에 가입되어 있는 경우
     */
    private void validateAlreadyJoinedMember(final Member member, final Club club) throws AlreadyJoinedMemberException {
        if (participantRepository.existsByMemberAndClub(member, club)) {
            throw new AlreadyJoinedMemberException();
        }
    }

    /**
     * 이미 가입 신청한 모임인지 확인
     *
     * @throws AlreadyRequestJoinClubException 이미 가입 신청한 경우
     */
    private void validateAlreadyRequested(final Member member, final Club club) throws AlreadyRequestJoinClubException {
        if (applicationFormRepository.existsByApplicantAndTarget(member, club)) {
            throw new AlreadyRequestJoinClubException();
        }
    }
}
