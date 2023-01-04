package com.mohaeng.application.club.eventhandler.participant;

import com.mohaeng.application.config.EventHandler;
import com.mohaeng.domain.club.event.role.CreateDefaultRoleEvent;
import com.mohaeng.domain.club.model.participant.Participant;
import com.mohaeng.domain.club.repository.participant.ParticipantRepository;
import com.mohaeng.domain.config.event.EventHistoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RegisterPresidentWithCreateDefaultRoleEventHandler extends EventHandler<CreateDefaultRoleEvent> {

    private final ParticipantRepository participantRepository;

    protected RegisterPresidentWithCreateDefaultRoleEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                                 final ParticipantRepository participantRepository) {
        super(eventHistoryRepository);
        this.participantRepository = participantRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Override
    public void handle(final CreateDefaultRoleEvent event) {
        Participant participant = new Participant(event.member(), event.club(), event.defaultPresidentRole());
        participantRepository.save(participant);

        process(event);
    }
}
