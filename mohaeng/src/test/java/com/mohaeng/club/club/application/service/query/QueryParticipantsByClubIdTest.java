package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import static com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase.Query;
import static com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase.Result;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_QUERY_PARTICIPANTS;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryParticipantsByClubId(모임에 속한 모든 참여자 조회) 은")
@ApplicationTest
class QueryParticipantsByClubIdTest {

    @Autowired
    private QueryParticipantsByClubIdUseCase queryParticipantsByClubIdUseCase;

    @Autowired
    private EntityManager em;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Club saveClubAndParticipants() {
        final Member member = saveMember(memberRepository, member(null));
        final Club club = saveClub(clubRepository, clubWithMember(member));
        club.registerParticipant(saveMember(memberRepository, member(null)));
        club.registerParticipant(saveMember(memberRepository, member(null)));
        club.registerParticipant(saveMember(memberRepository, member(null)));
        em.flush();
        em.clear();
        return club;
    }

    @Test
    void 모임_id로_해당_모임의_참여자를_조회할_수_있다() {
        // given
        final Club club = saveClubAndParticipants();
        final Participant president = club.findPresident();

        // when
        final Page<Result> results = queryParticipantsByClubIdUseCase.query(
                new Query(president.member().id(), club.id())
        );

        // then
        assertThat(results.getTotalElements()).isEqualTo(club.participants().participants().size());
    }

    @Test
    void 모임에_가입되지_않은_사람의_요청인_경우_예외가_발생한다() {
        // given
        final Club club = saveClubAndParticipants();

        // when
        final BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                queryParticipantsByClubIdUseCase.query(
                        new Query(100L, club.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_QUERY_PARTICIPANTS);
    }

    @Test
    void 모임이_없는경우_예외가_발생한다() {
        // given
        final Club club = saveClubAndParticipants();

        // when
        final BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                queryParticipantsByClubIdUseCase.query(
                        new Query(club.findPresident().member().id(), 100L)
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
    }
}