package com.mohaeng.participant.application.service;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.application.usecase.ChangeClubRoleNameUseCase;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NOT_FOUND_CLUB_ROLE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_ROLE_NAME;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.generalRole;
import static com.mohaeng.common.fixtures.ClubRoleFixture.officerRole;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

@ApplicationTest
@DisplayName("ChangeClubRoleName 는 ")
class ChangeClubRoleNameTest {

    @Autowired
    private ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EntityManager em;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @ParameterizedTest(name = "[{arguments}] 회장과 관리자는 모든 역할의 이름을 변경할 수 있다.")
        @EnumSource(mode = INCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void success_test_1(final ClubRoleCategory category) {
            // given
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            ClubRole role = clubRoleCategoryClubRoleMap.get(category);
            Member requester = saveMember();
            saveParticipant(requester, club, role);

            String originalGeneralRoleName = "변경전 일반회원";
            String originalOfficerRoleName = "변경전 임원";
            ClubRole makeGeneralRole = saveGeneralClubRole(originalGeneralRoleName, club);
            ClubRole makeOfficerRole = saveOfficerClubRole(originalOfficerRoleName, club);

            List<Long> roleIds = clubRoleCategoryClubRoleMap.values()
                    .stream().map(BaseEntity::id)
                    .collect(Collectors.toList());
            roleIds.add(makeGeneralRole.id());
            roleIds.add(makeOfficerRole.id());

            for (final Long roleId : roleIds) {
                // when
                String changeName = "변경 후 이름 " + roleId;
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(requester.id(), roleId, changeName)
                );

                // then
                assertThat(clubRoleRepository.findById(roleId).get().name())
                        .isEqualTo(changeName);
            }
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("일반 회원은 역할의 이름을 변경할 수 없다.")
        void fail_test_1() {
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            ClubRole role = clubRoleCategoryClubRoleMap.get(GENERAL);
            Member requester = saveMember();
            saveParticipant(requester, club, role);

            String originalGeneralRoleName = "변경전 일반회원";
            String originalOfficerRoleName = "변경전 임원";
            ClubRole makeGeneralRole = saveGeneralClubRole(originalGeneralRoleName, club);
            ClubRole makeOfficerRole = saveOfficerClubRole(originalOfficerRoleName, club);

            List<Long> roleIds = clubRoleCategoryClubRoleMap.values()
                    .stream().map(BaseEntity::id)
                    .collect(Collectors.toList());
            roleIds.add(makeGeneralRole.id());
            roleIds.add(makeOfficerRole.id());

            for (final Long roleId : roleIds) {
                // when
                String changeName = "변경 후 이름 " + roleId;
                BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                        changeClubRoleNameUseCase.command(
                                new ChangeClubRoleNameUseCase.Command(requester.id(), roleId, changeName)
                        )
                ).exceptionType();

                // then
                assertThat(baseExceptionType)
                        .isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
            }
        }

        @Test
        @DisplayName("회원을 찾을 수 없는 경우 예외가 발생한다.")
        void fail_test_2() {
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            ClubRole role = clubRoleCategoryClubRoleMap.get(OFFICER);
            Member requester = saveMember();
            saveParticipant(requester, club, role);

            // when
            String changeName = "변경 후 이름 ";
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    changeClubRoleNameUseCase.command(
                            new ChangeClubRoleNameUseCase.Command(
                                    requester.id() + 100L,
                                    clubRoleCategoryClubRoleMap.get(OFFICER).id(),
                                    changeName)
                    )
            ).exceptionType();

            // then
            assertThat(baseExceptionType)
                    .isEqualTo(NOT_FOUND_PARTICIPANT);
        }

        @Test
        @DisplayName("바꿀 역할을 찾을 수 없는 경우 예외가 발생한다.")
        void fail_test_3() {
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            ClubRole role = clubRoleCategoryClubRoleMap.get(OFFICER);
            Member requester = saveMember();
            saveParticipant(requester, club, role);

            // when
            String changeName = "변경 후 이름 ";
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    changeClubRoleNameUseCase.command(
                            new ChangeClubRoleNameUseCase.Command(
                                    requester.id(),
                                    clubRoleCategoryClubRoleMap.get(OFFICER).id() + 100L,
                                    changeName)
                    )
            ).exceptionType();

            // then
            assertThat(baseExceptionType)
                    .isEqualTo(NOT_FOUND_CLUB_ROLE);
        }
    }

    private ClubRole saveGeneralClubRole(final String name, final Club club) {
        return clubRoleRepository.save(generalRole(name, club));
    }

    private ClubRole saveOfficerClubRole(final String name, final Club club) {
        return clubRoleRepository.save(officerRole(name, club));
    }

    private Participant saveParticipant(final Member member, final Club club, final ClubRole clubRole) {
        return participantRepository.save(participant(null, member, club, clubRole));
    }

    private Map<ClubRoleCategory, ClubRole> saveDefaultClubRoles(final Club club) {
        return clubRoleRepository.saveAll(ClubRole.defaultRoles(club))
                .stream()
                .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));
    }

    private Club saveClub() {
        return clubRepository.save(club(null));
    }

    private Member saveMember() {
        return memberRepository.save(member(null));
    }
}