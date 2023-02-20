package com.mohaeng.club.applicationform.application.service;

import com.mohaeng.club.applicationform.application.usecase.RejectApplicationFormUseCase;
import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.club.applicationform.domain.event.OfficerRejectApplicationEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.common.event.Events;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;

@Service
@Transactional
public class RejectApplicationForm implements RejectApplicationFormUseCase {

    private final ApplicationFormRepository applicationFormRepository;

    public RejectApplicationForm(final ApplicationFormRepository applicationFormRepository) {
        this.applicationFormRepository = applicationFormRepository;
    }

    @Override
    public void command(final Command command) {
        ApplicationForm applicationForm = applicationFormRepository.findById(command.applicationFormId())
                .orElseThrow(() -> new ApplicationFormException(NOT_FOUND_APPLICATION_FORM));

        Participant manager = applicationForm.club().findParticipantByMemberId(command.managerId());

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
        if (manager.isPresident()) {
            return;
        }
        Participant president = applicationForm.club().findPresident();
        Events.raise(new OfficerRejectApplicationEvent(this,
                president.member().id(),
                manager.member().id(),
                manager.id(),
                applicationForm.applicant().id(),
                applicationForm.id()
        ));
    }
}
