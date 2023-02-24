package com.mohaeng.club.club.infrastructure.persistence.database.repository;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.mohaeng.club.club.domain.model.QClub.club;
import static java.util.Optional.ofNullable;

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
}
