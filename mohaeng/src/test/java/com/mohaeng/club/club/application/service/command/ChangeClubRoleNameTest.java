package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.usecase.ChangeClubRoleNameUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_ROLE_NAME;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveGeneral;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveOfficer;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ChangeClubRoleName 은")
@ApplicationTest
class ChangeClubRoleNameTest {

    @Autowired
    private ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager em;

    private Club club;
    private Member presidentMember;
    private Participant president;
    private Participant officer;
    private Participant general;
    private Map<ClubRoleCategory, ClubRole> clubRoleMap;

    @BeforeEach
    void init() {
        presidentMember = saveMember(memberRepository, member(null));
        club = saveClub(clubRepository, clubWithMember(presidentMember));
        president = club.participants().findByMemberId(presidentMember.id()).get();
        officer = saveOfficer(memberRepository, club);
        general = saveGeneral(memberRepository, club);
        flushAndClear();
        clubRoleMap = club.clubRoles().clubRoles()
                .stream()
                .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
        club = clubRepository.findById(club.id()).get();
    }

    @ParameterizedTest(name = "회장은 모든 역할의 이름을 변경할 수 있다")
    @EnumSource(mode = EXCLUDE)
    void 회장은_모든_역할의_이름을_변경할_수_있다(final ClubRoleCategory category) {
        // given
        String name = "변경할이름";

        // when
        changeClubRoleNameUseCase.command(
                new ChangeClubRoleNameUseCase.Command(
                        presidentMember.id(),
                        club.id(),
                        clubRoleMap.get(category).id(),
                        name
                ));
        flushAndClear();

        // then
        assertThat(club.findRoleById(clubRoleMap.get(category).id()).name())
                .isEqualTo(name);
    }

    @Test
    void 임원은_일반_역할의_이름만_변경할_수_있다() {
        // given
        String name = "변경할이름";

        // when
        changeClubRoleNameUseCase.command(
                new ChangeClubRoleNameUseCase.Command(
                        officer.member().id(),
                        club.id(),
                        clubRoleMap.get(GENERAL).id(),
                        name
                ));
        flushAndClear();

        // then
        assertThat(club.findRoleById(clubRoleMap.get(GENERAL).id()).name())
                .isEqualTo(name);
    }

    @ParameterizedTest(name = "임원이 일반 역할이 아닌 다른 역할의 이름을 변경할 경우 예외가 발생한다")
    @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"GENERAL"})
    void 임원이_일반_역할이_아닌_다른_역할의_이름을_변경할_경우_예외가_발생한다(final ClubRoleCategory category) {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                officer.member().id(),
                                club.id(),
                                clubRoleMap.get(category).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(club.findRoleById(clubRoleMap.get(category).id()).name())
                .isNotEqualTo(name);
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
    }

    @ParameterizedTest(name = "일반 회원은 역할의 이름을 변경할 수 없다")
    @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class)
    void 일반_회원은_역할의_이름을_변경할_수_없다(final ClubRoleCategory category) {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                general.member().id(),
                                club.id(),
                                clubRoleMap.get(category).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(club.findRoleById(clubRoleMap.get(category).id()).name())
                .isNotEqualTo(name);
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
    }

    @Test
    void 회원을_찾을_수_없는_경우_예외가_발생한다() {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                10000L,
                                club.id(),
                                clubRoleMap.get(PRESIDENT).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(club.findRoleById(clubRoleMap.get(PRESIDENT).id()).name())
                .isNotEqualTo(name);
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
    }

    @Test
    void 바꿀_역할을_찾을_수_없는_경우_예외가_발생한다() {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                general.member().id(),
                                club.id(),
                                10000L,
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
    }

    @Test
    void 모임이_없는_경우_예외가_발생한다() {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                general.member().id(),
                                club.id() + 1233L,
                                clubRoleMap.get(PRESIDENT).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
    }
}