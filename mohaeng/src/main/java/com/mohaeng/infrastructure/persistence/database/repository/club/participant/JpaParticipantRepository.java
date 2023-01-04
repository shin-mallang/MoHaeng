package com.mohaeng.infrastructure.persistence.database.repository.club.participant;

import com.mohaeng.domain.club.model.participant.Participant;
import com.mohaeng.domain.club.repository.participant.ParticipantRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepository {
}
