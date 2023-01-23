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
     * memberId와 clubId로 Participant 단일 조회
     * <p>
     * (Member 와 Club 으로 조회할 수 있으나, 그럼 Member 조회 + Club 조회를 위한 Select 쿼리가 2번 발생하며 ID만을 사용하도록 함)
     */
    Optional<Participant> findByMemberIdAndClubId(final Long memberId, final Long clubId);

    /**
     * 주어진 Club과, Member의 ID를 통해,
     * Club에서 해당 Member의 Participant ID를 조회
     */
    Optional<Participant> findWithClubRoleByMemberIdAndClub(final Long managerId, final Club club);

    /**
     * 해당 모임의 회장을 조회
     */
    Optional<Participant> findPresidentWithMemberByClub(final Club club);

    Optional<Participant> findById(final Long id);

    Optional<Participant> findWithMemberById(final Long id);

    void delete(final Participant participant);
}
