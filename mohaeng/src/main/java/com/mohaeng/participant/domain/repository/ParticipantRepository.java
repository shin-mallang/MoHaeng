package com.mohaeng.participant.domain.repository;

import com.mohaeng.participant.domain.model.Participant;

public interface ParticipantRepository {

    Participant save(final Participant participant);
}
