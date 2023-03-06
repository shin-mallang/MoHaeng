package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.domain.repository.ParticipantQueryRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_QUERY_PARTICIPANTS;

@Service
@Transactional(readOnly = true)
public class QueryParticipantsByClubId implements QueryParticipantsByClubIdUseCase {

    private final ClubRepository clubRepository;
    private final ParticipantQueryRepository participantQueryRepository;

    public QueryParticipantsByClubId(final ClubRepository clubRepository, final ParticipantQueryRepository participantQueryRepository) {
        this.clubRepository = clubRepository;
        this.participantQueryRepository = participantQueryRepository;
    }

    @Override
    public Page<Result> query(final Query query) {
        final Club club = clubRepository.findById(query.clubId()).orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));

        validateAuthorityQueryParticipants(query, club);

        return participantQueryRepository.findAllWithClubRoleAndMemberByClubId(query.clubId())
                .map(Result::from);
    }

    private void validateAuthorityQueryParticipants(final Query query, final Club club) {
        if (!club.existParticipantByMemberId(query.memberId())) {
            throw new ParticipantException(NO_AUTHORITY_QUERY_PARTICIPANTS);
        }
    }
}
