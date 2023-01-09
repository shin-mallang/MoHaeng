package com.mohaeng.participant.domain.repository;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.domain.model.Participant;

import java.util.List;

public interface ParticipantRepository {

    Participant save(final Participant participant);

    boolean existsByMemberAndClub(final Member member, final Club club);

    List<Participant> findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(final Long clubId);
}
