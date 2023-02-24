package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase.Query;
import static com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase.Result;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryClubById(모임 단일 조회) 는")
@ApplicationTest
class QueryClubByIdTest {

    @Autowired
    private QueryClubByIdUseCase queryClubByIdUseCase;

    @Autowired
    private EntityManager em;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    private Club club;

    @BeforeEach
    void init() {
        member = saveMember(memberRepository, member(null));
        club = saveClub(clubRepository, clubWithMember(member));
        em.flush();
        em.clear();
        System.out.println("========== AFTER SETTING ==========");
    }

    @Test
    void id로_모임을_조회한다() {
        // when
        Result result = queryClubByIdUseCase.query(
                new Query(club.id())
        );

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(club.id()),
                () -> assertThat(result.name()).isEqualTo(club.name()),
                () -> assertThat(result.description()).isEqualTo(club.description()),
                () -> assertThat(result.currentParticipantCount()).isEqualTo(club.currentParticipantCount()),
                () -> assertThat(result.maxParticipantCount()).isEqualTo(club.maxParticipantCount())
        );
    }

    @Test
    void 모임이_없는_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                queryClubByIdUseCase.query(
                        new Query(10L)
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
    }
}