package com.mohaeng.club.club.domain.repository;

import com.mohaeng.club.club.domain.model.Participant;
import org.springframework.data.domain.Page;

public interface ParticipantQueryRepository {

    Page<Participant> findAllWithClubRoleAndMemberByClubId(final Long clubId);
}
