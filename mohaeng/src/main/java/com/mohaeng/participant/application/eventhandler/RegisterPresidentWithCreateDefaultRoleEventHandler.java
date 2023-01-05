package com.mohaeng.participant.application.eventhandler;

import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RegisterPresidentWithCreateDefaultRoleEventHandler extends EventHandler<CreateDefaultRoleEvent> {

    private final ParticipantRepository participantRepository;

    public RegisterPresidentWithCreateDefaultRoleEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                              final ParticipantRepository participantRepository) {
        super(eventHistoryRepository);
        this.participantRepository = participantRepository;
    }

    @Transactional
    @EventListener
    @Override
    public void handle(final CreateDefaultRoleEvent event) {

        // TODO Club club = ClubRepository.findById(event.club().id());
        Participant participant = new Participant(event.member(), event.club(), event.defaultPresidentRole());

        // TODO   Participant participant = new Participant(event.member());
        // participant.joinClub(event.club(), event.defaultPresidentRole());
        // void joinClub() {
        //      this.club = club;
        //      this.defaultPresidentRole = defaultPresidentRole;
        //      club.peopleCountUp() -> club.participantCountUp() [maxPeopleCount -> maxParticipantCount 로 변경]
        // }
        participantRepository.save(participant);
        process(event);
    }
}
