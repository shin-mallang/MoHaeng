package com.mohaeng.club.club.application.service;

import com.mohaeng.club.club.application.usecase.CreateClubRoleUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveGeneral;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveOfficer;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("CreateClubRole 은 ")
@ApplicationTest
class CreateClubRoleTest {

    @Autowired
    private CreateClubRoleUseCase createClubRoleUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager em;

    private Club club;
    private Member presidentMember;
    private Participant officer;
    private Participant general;

    @BeforeEach
    void init() {
        presidentMember = saveMember(memberRepository, member(null));
        club = saveClub(clubRepository, clubWithMember(presidentMember));
        officer = saveOfficer(memberRepository, club);
        general = saveGeneral(memberRepository, club);
        flushAndClear();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
        club = clubRepository.findById(club.id()).get();
    }

    @Test
    void 회장_혹은_임원은_새로운_역할을_생성할_수_있으며_새로_생성된_역할은_기본_역할이_아니다() {
        // given
        final String generalRoleName = "새로생성한 일반회원역할";
        final String officerRoleName = "새로생성한 임원역할";

        // when
        createClubRoleUseCase.command(
                new CreateClubRoleUseCase.Command(
                        presidentMember.id(), club.id(),
                        generalRoleName, GENERAL
                ));
        createClubRoleUseCase.command(
                new CreateClubRoleUseCase.Command(
                        officer.member().id(), club.id(),
                        officerRoleName, OFFICER
                ));
        flushAndClear();

        // then
        Map<ClubRoleCategory, ClubRole> created = club.clubRoles().clubRoles()
                .stream()
                .filter(it -> !it.isDefault())
                .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));
        assertAll(
                () -> assertThat(created.get(GENERAL).name()).isEqualTo(generalRoleName),
                () -> assertThat(created.get(OFFICER).name()).isEqualTo(officerRoleName)
        );
    }

    @Test
    void 일반_회원이_역할을_생성하려는_경우_예외가_발생한다() {
        // given
        final String generalRoleName = "새로생성한 일반회원역할";
        int before = club.clubRoles().clubRoles().size();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                general.id(), club.id(),
                                generalRoleName, GENERAL
                        ))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CREATE_ROLE);
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
    }

    @Test
    void 회원_역할은_새로_생성할_수_없다() {
        // given
        final String roleName = "새로생성한 회장역할";
        int before = club.clubRoles().clubRoles().size();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id(),
                                roleName,
                                ClubRoleCategory.PRESIDENT
                        )
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(CAN_NOT_CREATE_PRESIDENT_ROLE);
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
    }

    @Test
    void 모임_내에서_역할_이름이_중복되는_경우_예외가_발생한다() {
        // given
        String duplicated = club.clubRoles().findDefaultRoleByCategory(GENERAL).name();
        int before = club.clubRoles().clubRoles().size();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id(),
                                duplicated,
                                OFFICER
                        )
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
    }
}