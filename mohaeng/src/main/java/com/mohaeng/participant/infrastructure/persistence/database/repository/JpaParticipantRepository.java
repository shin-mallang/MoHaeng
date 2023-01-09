package com.mohaeng.participant.infrastructure.persistence.database.repository;

import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepository {

    @Override
    @Query("select p from Participant p join fetch p.member where p.clubRole.clubRoleCategory = 'PRESIDENT' or p.clubRole.clubRoleCategory = 'OFFICER'")
    List<Participant> findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(final Long clubId);
}
