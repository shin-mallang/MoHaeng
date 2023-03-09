package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryParticipatedClubUseCase;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class QueryParticipatedClub implements QueryParticipatedClubUseCase {

    private final ClubQueryRepository clubQueryRepository;

    public QueryParticipatedClub(final ClubQueryRepository clubQueryRepository) {
        this.clubQueryRepository = clubQueryRepository;
    }

    @Override
    public Page<Result> query(final Query query) {
        return clubQueryRepository.findAllByMemberId(query.memberId(), query.pageable())
                .map(Result::from);
    }
}
