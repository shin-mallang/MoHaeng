package com.mohaeng.participant.domain.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.domain.model.Participant;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository {

    Participant save(final Participant participant);

    boolean existsByMemberAndClub(final Member member, final Club club);

    List<Participant> findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(final Long clubId);

    /**
     * 주어진 Club과, Member의 ID를 통해,
     * Club에서 해당 Member의 Participant ID를 조회
     */
    Optional<Participant> findWithClubRoleByMemberIdAndClub(final Long managerId, final Club club);

    /**
     * 해당 모임의 회장을 조회
     */
    Optional<Participant> findPresidentWithMemberByClub(final Club club);
}
