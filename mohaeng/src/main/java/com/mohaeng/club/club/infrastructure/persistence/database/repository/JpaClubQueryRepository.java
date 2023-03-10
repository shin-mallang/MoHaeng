package com.mohaeng.club.club.infrastructure.persistence.database.repository;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.domain.model.QClub.club;
import static com.mohaeng.club.club.domain.model.QParticipant.participant;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class JpaClubQueryRepository implements ClubQueryRepository {

    private final JPAQueryFactory query;

    public JpaClubQueryRepository(final JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Optional<Club> findById(final Long id) {
        return ofNullable(query.selectFrom(club)
                .where(club.id.eq(id))
                .fetchOne());
    }

    @Override
    public Page<Club> findAllBySearchCond(final ClubSearchCond clubSearchCond, final Pageable pageable) {
        List<Club> contents = query.selectFrom(club)
                .where(nameLike(clubSearchCond.name()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query.select(club.count())
                .from(club)
                .where(nameLike(clubSearchCond.name()));

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Club> findAllByMemberId(final Long memberId, final Pageable pageable) {
        final List<Club> contents = query.selectFrom(club)
                .join(participant)
                .on(participant.club.eq(club))
                .where(participant.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final JPAQuery<Long> countQuery = query.select(club.count())
                .join(participant)
                .on(participant.club.eq(club))
                .where(participant.member.id.eq(memberId));

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    private BooleanExpression nameLike(final String name) {
        return hasText(name) ? club.name.likeIgnoreCase("%" + name + "%") : null;
    }
}
