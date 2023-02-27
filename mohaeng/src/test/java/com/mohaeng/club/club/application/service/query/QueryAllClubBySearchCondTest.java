package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase.Query;
import static com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase.Result;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryAllClubBySearchCond(검색 조건으로 모든 모임 찾기) 은")
@ApplicationTest
class QueryAllClubBySearchCondTest {

    @Autowired
    private QueryAllClubBySearchCondUseCase queryAllClubBySearchCondUseCase;

    @Autowired
    private EntityManager em;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void init() {
        Member member = saveMember(memberRepository, member(null));
        saveClub(clubRepository, new Club("AAA 1", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("AA A 2", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("aaa 3", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("aa a 4", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("bb 5", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("bb a 6", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("모임 이름입니다. 7", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("모임 이름일까요. 8", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("모임이 름입니다. 9", "~~~ 동아리", 100, member));
        saveClub(clubRepository, new Club("10개만 할게요. 10", "~~~ 동아리", 100, member));
        em.flush();
        em.clear();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "", "    "})
    void 검색_조건중_문자열이_없거나_빈칸인_경우_모든_모임을_조회한다(final String input) {
        // when
        Page<Result> result = queryAllClubBySearchCondUseCase.query(
                new Query(new ClubQueryRepository.ClubSearchCond(input), PageRequest.of(0, 10))
        );

        // then
        assertAll(
                () -> assertThat(result.getTotalElements()).isEqualTo(10),
                () -> assertThat(result.getTotalPages()).isEqualTo(1),
                () -> assertThat(result.getNumber()).isEqualTo(0),
                () -> assertThat(result.getNumberOfElements()).isEqualTo(10)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "AA -> 4",
            "모임 -> 3",
            "이름 -> 2",
            "10 -> 1",
    }, delimiterString = " -> ")
    void 검색_조건에_이름이_있는_경우_해당_이름을_포함한_모든_모임을_검색한다(final String name, final int count) {
        // given
        final int size = 10;

        // when
        Page<Result> result = queryAllClubBySearchCondUseCase.query(
                new Query(new ClubQueryRepository.ClubSearchCond(name), PageRequest.of(0, 10))
        );

        // then
        assertAll(
                () -> assertThat(result.getTotalElements()).isEqualTo(count),
                () -> assertThat(result.getTotalPages()).isEqualTo(1),
                () -> assertThat(result.getNumber()).isEqualTo(0),
                () -> assertThat(result.getSize()).isEqualTo(size),
                () -> assertThat(result.getNumberOfElements()).isEqualTo(count)
        );
    }
}