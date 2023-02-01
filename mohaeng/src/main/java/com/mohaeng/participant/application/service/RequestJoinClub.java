package com.mohaeng.participant.application.service;

import com.mohaeng.applicationform.application.usecase.RequestJoinClubUseCase;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.common.event.Events;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.member.exception.MemberException;
import com.mohaeng.participant.domain.event.ClubJoinApplicationCreatedEvent;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_REQUEST_JOIN_CLUB;
import static com.mohaeng.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

@Service
@Transactional
public class RequestJoinClub implements RequestJoinClubUseCase {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationFormRepository applicationFormRepository;

    public RequestJoinClub(final ClubRepository clubRepository,
                           final MemberRepository memberRepository,
                           final ParticipantRepository participantRepository,
                           final ApplicationFormRepository applicationFormRepository) {
        this.clubRepository = clubRepository;
        this.memberRepository = memberRepository;
        this.participantRepository = participantRepository;
        this.applicationFormRepository = applicationFormRepository;
    }

    @Override
    public Long command(final Command command) {
        Member member = memberRepository.findById(command.applicantId()).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        Club club = clubRepository.findById(command.targetClubId()).orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));

        // 이미 가입된 회원이거나, 이미 가입 신청을 한 회원이라면 오류
        validate(member, club);

        // 가입 신청서 저장
        ApplicationForm applicationForm = ApplicationForm.create(member, club);
        applicationFormRepository.save(applicationForm);

        // 이벤트 발행 -> 모임 가입 신청 요청에 대하여 회장과 임원진에게 알림 보내기
        raiseEvents(member, club, applicationForm);

        return applicationForm.id();
    }

    /**
     * 이미 가입된 모임이거나, 이미 가입 신청을 한 회원인지 확인
     *
     * @throws ApplicationFormException - (ALREADY_MEMBER_JOINED_CLUB) 이미 회원이 모임에 가입되어 있는 경우
     *                                  - (ALREADY_REQUEST_JOIN_CLUB) 이미 가입 신청한 경우
     */
    private void validate(final Member member, final Club club) throws ApplicationFormException {
        // 이미 가입되어있는지 확인
        validateAlreadyJoinedMember(member, club);

        // 이미 가입 신청 하였는지 확인
        validateAlreadyRequested(member, club);
    }

    /**
     * 이미 회원이 모임에 가입되어 있는지 검증
     *
     * @throws ApplicationFormException (ALREADY_MEMBER_JOINED_CLUB) 이미 회원이 모임에 가입되어 있는 경우
     */
    private void validateAlreadyJoinedMember(final Member member, final Club club) throws ApplicationFormException {
        if (participantRepository.existsByMemberAndClub(member, club)) {
            throw new ApplicationFormException(ALREADY_MEMBER_JOINED_CLUB);
        }
    }

    /**
     * 이미 가입 신청한 모임인지 확인
     *
     * @throws ApplicationFormException (ALREADY_REQUEST_JOIN_CLUB) 이미 가입 신청한 경우
     */
    private void validateAlreadyRequested(final Member member, final Club club) throws ApplicationFormException {
        if (applicationFormRepository.existsByApplicantAndTargetAndProcessedFalse(member, club)) {
            throw new ApplicationFormException(ALREADY_REQUEST_JOIN_CLUB);
        }
    }

    /**
     * 이벤트 발행 -> 모임 가입 신청 요청에 대하여 회장과 임원진에게 알림 보내기
     */
    private void raiseEvents(final Member applicant, final Club club, final ApplicationForm applicationForm) {
        Events.raise(new ClubJoinApplicationCreatedEvent(
                this,
                getOfficerAndPresidentIdsOfClub(club),
                club.id(),
                applicant.id(),
                applicationForm.id()
        ));
    }

    /**
     * 모임의 임원진과 회장의 Member Id를 반환한다.
     */
    private List<Long> getOfficerAndPresidentIdsOfClub(final Club club) {
        return participantRepository.findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(club.id())
                .stream()
                .map(Participant::member)
                .map(BaseEntity::id)
                .toList();
    }
}
