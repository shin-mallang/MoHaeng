package com.mohaeng.club.club.infrastructure.persistence.database.repository;

import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ParticipantQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mohaeng.club.club.domain.model.QParticipant.participant;

@Repository
public class JpaParticipantQueryRepository implements ParticipantQueryRepository {

    private final JPAQueryFactory query;

    public JpaParticipantQueryRepository(final JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Page<Participant> findAllWithClubRoleAndMemberByClubId(final Long clubId) {
        final List<Participant> contents = query.selectFrom(participant)
                .where(participant.club.id.eq(clubId))
                .join(participant.clubRole).fetchJoin()
                .join(participant.member).fetchJoin()
                .fetch();

        return new PageImpl<>(contents);
    }
}
