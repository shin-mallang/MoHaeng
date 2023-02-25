package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository;
import com.mohaeng.club.club.exception.ClubException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;

@Service
@Transactional(readOnly = true)
public class QueryClubById implements QueryClubByIdUseCase {

    private final ClubQueryRepository clubQueryRepository;

    public QueryClubById(final ClubQueryRepository clubQueryRepository) {
        this.clubQueryRepository = clubQueryRepository;
    }

    @Override
    public Result query(final Query query) {
        return Result.from(clubQueryRepository.findById(query.id())
                .orElseThrow(() -> new ClubException(NOT_FOUND_CLUB)));
    }
}
