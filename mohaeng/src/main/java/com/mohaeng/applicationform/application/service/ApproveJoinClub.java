package com.mohaeng.applicationform.application.service;

import com.mohaeng.applicationform.application.usecase.ApproveJoinClubUseCase;
import com.mohaeng.applicationform.domain.event.ApproveJoinClubEvent;
import com.mohaeng.applicationform.domain.event.OfficerApproveClubJoinApplicationEvent;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.clubrole.exception.ClubRoleExceptionType;
import com.mohaeng.common.event.Events;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PRESIDENT;

@Service
@Transactional
public class ApproveJoinClub implements ApproveJoinClubUseCase {

    private final ApplicationFormRepository applicationFormRepository;
    private final ClubRoleRepository clubRoleRepository;
    private final ParticipantRepository participantRepository;

    public ApproveJoinClub(final ApplicationFormRepository applicationFormRepository,
                           final ClubRoleRepository clubRoleRepository,
                           final ParticipantRepository participantRepository) {
        this.applicationFormRepository = applicationFormRepository;
        this.clubRoleRepository = clubRoleRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void command(final Command command) {
        // 가입 신청서 조회
        ApplicationForm applicationForm = applicationFormRepository.findWithClubById(command.applicationFormId())
                .orElseThrow(() -> new ApplicationFormException(NOT_FOUND_APPLICATION_FORM));

        // 처리한 회원 조회
        Participant manager = participantRepository.findWithClubRoleByMemberIdAndClub(command.managerId(), applicationForm.target())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        // 모임의 기본 역할 조회
        ClubRole defaultGeneralRole = clubRoleRepository.findDefaultGeneralRoleByClub(manager.club())
                .orElseThrow(() -> new ClubRoleException(ClubRoleExceptionType.NOT_FOUND_CLUB_ROLE));

        // 신청자가 이미 모임에 가입되어 있는지 체크
        checkApplicantAlreadyJoinClub(applicationForm.applicant(), applicationForm.target());

        // 가입 신청 승인 -> 모임에 가입시키기
        Participant applicant = manager.acceptJoinClub(applicationForm, defaultGeneralRole);
        participantRepository.save(applicant);

        // 알림 전송을 위해 모임의 회장 조회하기
        Participant president = participantRepository.findPresidentWithMemberByClub(manager.club())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PRESIDENT));

        raiseEvent(applicationForm, manager, applicant, president);
    }

    /**
     * 이미 신청자가 모임에 가입되어 있는지 검사한다.
     *
     * @throws ApplicationFormException 이미 신청자가 모임에 가입되어 있는 경우
     */
    private void checkApplicantAlreadyJoinClub(final Member applicant, final Club target) throws ApplicationFormException {
        if (participantRepository.existsByMemberAndClub(applicant, target)) {
            throw new ApplicationFormException(ALREADY_MEMBER_JOINED_CLUB);
        }
    }

    /**
     * 이벤트를 발행한다.
     */
    private void raiseEvent(final ApplicationForm applicationForm,
                            final Participant manager,
                            final Participant applicant,
                            final Participant president) {

        // 모임 가입 요청 승인 이벤트 -> 가입된 회원에게 가입되었다는 알림 전송
        Events.raise(new ApproveJoinClubEvent(this,
                applicant.id(),
                applicationForm.target().id())
        );

        // 처리자가 회장이 아닌 경우 회장에게 알림 전송을 위한 이벤트 발행
        if (!president.equals(manager)) {
            Events.raise(new OfficerApproveClubJoinApplicationEvent(this,
                    president.member().id(),
                    manager.id(),
                    applicant.id(),
                    applicationForm.id())
            );
        }
    }
}
