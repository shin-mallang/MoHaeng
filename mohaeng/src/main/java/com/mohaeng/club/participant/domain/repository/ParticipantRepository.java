package com.mohaeng.club.participant.domain.repository;

import com.mohaeng.club.participant.domain.model.Participant;

import java.util.Optional;

public interface ParticipantRepository {

    Optional<Participant> findByMemberIdAndClubId(final Long memberId, final Long clubId);

    Optional<Participant> findPresidentByClubId(final Long clubId);
}
