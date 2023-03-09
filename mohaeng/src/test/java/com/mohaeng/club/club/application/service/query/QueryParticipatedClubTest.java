package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.QueryParticipatedClubUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static com.mohaeng.club.club.application.usecase.query.QueryParticipatedClubUseCase.Query;
import static com.mohaeng.club.club.application.usecase.query.QueryParticipatedClubUseCase.Result;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryParticipatedClub(회원이 가입한 모임 조회) 은")
@ApplicationTest
class QueryParticipatedClubTest {

    @Autowired
    private QueryParticipatedClubUseCase queryParticipatedClubUseCase;

    @Autowired
    private EntityManager em;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final List<Club> myClubs = new ArrayList<>();

    private Member me;

    @BeforeEach
    void init() {
        me = saveMember(memberRepository, member(null));
        Member other = saveMember(memberRepository, member(null));
        myClubs.add(saveClub(clubRepository, new Club("sampleName1", "des", 100, me)));
        myClubs.add(saveClub(clubRepository, new Club("sampleName2", "des", 100, me)));
        saveClub(clubRepository, new Club("sampleName3", "des", 100, other));
        saveClub(clubRepository, new Club("sampleName4", "des", 100, other));
        em.flush();
        em.clear();

    }

    @Test
    void 자신이_가입한_모임만을_조회한다() {
        // given

        // when
        final Page<Result> query = queryParticipatedClubUseCase.query(
                new Query(me.id(), PageRequest.of(0, 100))
        );

        // then
        assertAll(
                () -> assertThat(query.getTotalElements()).isEqualTo(myClubs.size()),
                () -> assertThat(query.getContent())
                        .extracting(Result::name)
                        .containsAll(myClubs.stream().map(Club::name).toList())
        );
    }
}
