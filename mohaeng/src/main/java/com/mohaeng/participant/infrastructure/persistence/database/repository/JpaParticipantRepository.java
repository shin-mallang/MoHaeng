package com.mohaeng.participant.infrastructure.persistence.database.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepository {

    @Override
    @Query("select p from Participant p join fetch p.member where p.clubRole.clubRoleCategory = 'PRESIDENT' or p.clubRole.clubRoleCategory = 'OFFICER'")
    List<Participant> findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(final Long clubId);

    @Override
    @Query("select p from Participant p join fetch p.clubRole where p.member.id = :managerId and p.club = :club")
    Optional<Participant> findWithClubRoleByMemberIdAndClub(@Param("managerId") final Long managerId,
                                                            @Param("club") final Club club);
}
