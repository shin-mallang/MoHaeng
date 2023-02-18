package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.usecase.ApproveApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.club.applicationform.domain.event.OfficerApproveApplicationEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.participant.domain.model.Participant;
import com.mohaeng.club.participant.domain.repository.ParticipantRepository;
import com.mohaeng.club.participant.exception.ParticipantException;
import com.mohaeng.common.event.Events;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.club.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.participant.exception.ParticipantExceptionType.NOT_FOUND_PRESIDENT;

@Service
@Transactional
public class ApproveApplicationForm implements ApproveApplicationFormUseCase {

    private final ApplicationFormRepository applicationFormRepository;
    private final ParticipantRepository participantRepository;

    public ApproveApplicationForm(final ApplicationFormRepository applicationFormRepository, final ParticipantRepository participantRepository) {
        this.applicationFormRepository = applicationFormRepository;
        this.participantRepository = participantRepository;

    }

    @Override
    public void command(final Command command) {
        ApplicationForm applicationForm = applicationFormRepository.findById(command.applicationFormId())
                .orElseThrow(() -> new ApplicationFormException(NOT_FOUND_APPLICATION_FORM));

        Participant manager = participantRepository.findByMemberIdAndClubId(command.managerId(), applicationForm.club().id())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        applicationForm.approve(manager);

        raiseEvent(applicationForm, manager);
    }

    private void raiseEvent(final ApplicationForm applicationForm, final Participant manager) {
        // 1. 가입된 회원에게 가입되었다는 알림
        Events.raise(ApplicationProcessedEvent.approve(this,
                applicationForm.applicant().id(),
                applicationForm.id(),
                applicationForm.club().id()));

        // 2. 회장이 아닌 경우, 회장에게 누가 가입 신청서를 승인했다는 알림
        Participant president = participantRepository.findPresidentByClubId(applicationForm.club().id())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PRESIDENT));
        if (president.id().equals(manager.id())) {
            return;
        }

        Long applicantId = applicationForm.applicant().id();
        Participant registerParticipant = applicationForm.club().findParticipantByMemberId(applicantId).orElseThrow(IllegalStateException::new);
        Events.raise(new OfficerApproveApplicationEvent(this,
                president.member().id(),
                manager.member().id(),
                manager.id(),
                applicantId,
                registerParticipant.id(),
                applicationForm.id()
        ));
    }
}
