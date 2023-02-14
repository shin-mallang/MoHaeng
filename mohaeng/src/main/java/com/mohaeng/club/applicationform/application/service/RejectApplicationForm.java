package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.usecase.RejectApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.club.applicationform.domain.event.OfficerRejectApplicationEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.participant.domain.model.Participant;
import com.mohaeng.club.participant.domain.repository.ParticipantRepository;
import com.mohaeng.club.participant.exception.ParticipantException;
import com.mohaeng.common.event.Events;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.club.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.participant.exception.ParticipantExceptionType.NOT_FOUND_PRESIDENT;

@Service
@Transactional
public class RejectApplicationForm implements RejectApplicationFormUseCase {

    private final ApplicationFormRepository applicationFormRepository;
    private final ParticipantRepository participantRepository;

    public RejectApplicationForm(final ApplicationFormRepository applicationFormRepository, final ParticipantRepository participantRepository) {
        this.applicationFormRepository = applicationFormRepository;
        this.participantRepository = participantRepository;

    }

    @Override
    public void command(final Command command) {
        ApplicationForm applicationForm = applicationFormRepository.findById(command.applicationFormId())
                .orElseThrow(() -> new ApplicationFormException(NOT_FOUND_APPLICATION_FORM));

        Participant manager = participantRepository.findByMemberIdAndClubId(command.managerId(), applicationForm.club().id())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        applicationForm.reject(manager);

        raiseEvent(applicationForm, manager);
    }

    private void raiseEvent(final ApplicationForm applicationForm, final Participant manager) {
        // 1. 거절된 회원에게 거절되었다는 알림
        Events.raise(ApplicationProcessedEvent.reject(this,
                applicationForm.applicant().id(),
                applicationForm.id(),
                applicationForm.club().id()));

        // 2. 회장이 아닌 경우, 회장에게 누가 가입 신청서를 거절했다는 알림
        Participant president = participantRepository.findPresidentByClubId(applicationForm.club().id())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PRESIDENT));
        if (president.id().equals(manager.id())) {
            return;
        }
        Events.raise(new OfficerRejectApplicationEvent(this,
                president.member().id(),
                manager.member().id(),
                manager.id(),
                applicationForm.applicant().id(),
                applicationForm.id()
        ));
    }
}
