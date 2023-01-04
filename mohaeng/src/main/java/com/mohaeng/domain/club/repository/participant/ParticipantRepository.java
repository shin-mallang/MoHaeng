package com.mohaeng.domain.club.repository.participant;

import com.mohaeng.domain.club.model.participant.Participant;

public interface ParticipantRepository {

    Participant save(final Participant participant);
}
