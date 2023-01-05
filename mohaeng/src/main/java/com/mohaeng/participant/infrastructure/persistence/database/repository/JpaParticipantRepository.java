package com.mohaeng.participant.infrastructure.persistence.database.repository;

import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepository {
}
