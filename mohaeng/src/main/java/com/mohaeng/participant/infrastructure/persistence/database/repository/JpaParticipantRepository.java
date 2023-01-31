package com.mohaeng.participant.infrastructure.persistence.database.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepository {

    @Override
    @Query("select p from Participant p join fetch p.member where p.clubRole.clubRoleCategory = 'PRESIDENT' or p.clubRole.clubRoleCategory = 'OFFICER'")
    List<Participant> findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(final Long clubId);

    @Override
    @Query("select p from Participant p join fetch p.clubRole where p.member.id = :memberId and p.club = :club")
    Optional<Participant> findWithClubRoleByMemberIdAndClub(@Param("memberId") final Long memberId,
                                                            @Param("club") final Club club);

    @Override
    @Query("select p from Participant p join fetch p.clubRole where p.member.id = :memberId and p.club.id = :clubId")
    Optional<Participant> findWithClubRoleByMemberIdAndClubId(@Param("memberId") final Long memberId,
                                                              @Param("clubId") final Long clubId);

    @Override
    @Query("select p from Participant p join fetch p.member where p.clubRole.clubRoleCategory = 'PRESIDENT'")
    Optional<Participant> findPresidentWithMemberByClub(final Club club);

    @Override
    @Query("select p from Participant p join fetch p.member join fetch p.club where p.id = :id")
    Optional<Participant> findWithMemberAndClubById(@Param("id") final Long id);

    @Override
    @Modifying
    @Query("delete from Participant p where p.club.id = :clubId")
    void deleteAllByClubId(@Param("clubId") final Long clubId);

    @Override
    @Query("select p from Participant p where p.club.id = :clubId")
    List<Participant> findAllWithMemberByClubId(final Long clubId);
}
