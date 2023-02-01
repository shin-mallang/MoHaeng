package com.mohaeng.participant.application.service;

import com.mohaeng.applicationform.application.usecase.RejectJoinClubUseCase;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.common.event.Events;
import com.mohaeng.participant.domain.event.ApplicationProcessedEvent;
import com.mohaeng.participant.domain.event.OfficerRejectClubJoinApplicationEvent;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PRESIDENT;

@Service
@Transactional
public class RejectJoinClub implements RejectJoinClubUseCase {

    private final ApplicationFormRepository applicationFormRepository;
    private final ParticipantRepository participantRepository;

    public RejectJoinClub(final ApplicationFormRepository applicationFormRepository,
                          final ParticipantRepository participantRepository) {
        this.applicationFormRepository = applicationFormRepository;
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

        // 가입 신청 거절 처리
        manager.rejectApplicationForm(applicationForm);

        // 알림 전송을 위해 모임의 회장 조회하기
        Participant president = participantRepository.findPresidentWithMemberByClub(manager.club())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PRESIDENT));

        raiseEvent(applicationForm, manager, president);
    }

    /**
     * 이벤트를 발행한다.
     */
    private void raiseEvent(final ApplicationForm applicationForm,
                            final Participant manager,
                            final Participant president) {

        // 모임 가입 요청 거절 이벤트 -> 거절된 회원에게 거절되었다는 알림 전송
        Events.raise(ApplicationProcessedEvent.reject(this,
                applicationForm.applicant().id(),
                applicationForm.id(),
                applicationForm.target().id())
        );

        // 처리자가 회장이 아닌 경우 회장에게 알림 전송을 위한 이벤트 발행
        if (!president.equals(manager)) {
            Events.raise(new OfficerRejectClubJoinApplicationEvent(this,
                    president.member().id(),
                    manager.member().id(),
                    manager.id(),
                    applicationForm.applicant().id(),
                    applicationForm.id())
            );
        }
    }
}
