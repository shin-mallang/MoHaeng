package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QueryAllClubBySearchCond implements QueryAllClubBySearchCondUseCase {

    private final ClubQueryRepository clubQueryRepository;

    public QueryAllClubBySearchCond(final ClubQueryRepository clubQueryRepository) {
        this.clubQueryRepository = clubQueryRepository;
    }

    @Override
    public Page<Result> query(final Query query, final Pageable pageable) {
        return clubQueryRepository.findAllBySearchCond(query.clubSearchCond(), pageable)
                .map(Result::from);
    }
}
