package com.mohaeng.club.participant.infrastructure.persistence.database.repository;

import com.mohaeng.club.participant.domain.model.Participant;
import com.mohaeng.club.participant.domain.repository.ParticipantRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepository {

    @Override
    @Query("select p from Participant p where p.member.id = :memberId and p.club.id = :clubId")
    Optional<Participant> findByMemberIdAndClubId(@Param("memberId") final Long memberId,
                                                  @Param("clubId") final Long clubId);

    @Override
    @Query("select p from Participant p where p.club.id = :clubId and p.clubRole.clubRoleCategory = 'PRESIDENT'")
    Optional<Participant> findPresidentByClubId(@Param("clubId") final Long clubId);
}
