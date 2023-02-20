package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.usecase.ApproveApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.club.applicationform.domain.event.OfficerApproveApplicationEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.common.event.Events;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;

@Service
@Transactional
public class ApproveApplicationForm implements ApproveApplicationFormUseCase {

    private final ApplicationFormRepository applicationFormRepository;

    public ApproveApplicationForm(final ApplicationFormRepository applicationFormRepository) {
        this.applicationFormRepository = applicationFormRepository;
    }

    @Override
    public void command(final Command command) {
        ApplicationForm applicationForm = applicationFormRepository.findById(command.applicationFormId())
                .orElseThrow(() -> new ApplicationFormException(NOT_FOUND_APPLICATION_FORM));

        Participant manager = applicationForm.club().findParticipantByMemberId(command.managerId());

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
        if (manager.isPresident()) {
            return;
        }
        Participant president = applicationForm.club().findPresident();
        Long applicantId = applicationForm.applicant().id();
        Participant registerParticipant = applicationForm.club().findParticipantByMemberId(applicantId);
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
