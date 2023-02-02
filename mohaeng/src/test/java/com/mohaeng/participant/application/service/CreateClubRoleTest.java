package com.mohaeng.participant.application.service;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.application.usecase.CreateClubRoleUseCase;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
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

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NO_AUTHORITY_CREATE_ROLE;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@ApplicationTest
@DisplayName("CreateClubRole 은 ")
class CreateClubRoleTest {

    @Autowired
    private CreateClubRoleUseCase createClubRoleUseCase;

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

        @ParameterizedTest(name = "[{arguments}] 회장 혹은 임원은 새로운 역할을 생성할 수 있으며, 새로 생성된 역할은 기본 역할이 아니다.")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"GENERAL"})
        void success_test_1(final ClubRoleCategory category) {
            // given
            final String generalRoleName = "새로생성한 일반회원역할";
            final String officerRoleName = "새로생성한 임원역할";

            Member requester = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            ClubRole role = clubRoleCategoryClubRoleMap.get(category);
            Participant participant = saveParticipant(requester, club, role);

            // when
            createClubRoleUseCase.command(
                    new CreateClubRoleUseCase.Command(
                            requester.id(),
                            club.id(),
                            generalRoleName,
                            ClubRoleCategory.GENERAL
                    )
            );
            createClubRoleUseCase.command(
                    new CreateClubRoleUseCase.Command(
                            requester.id(),
                            club.id(),
                            officerRoleName,
                            ClubRoleCategory.OFFICER
                    )
            );

            List<ClubRole> resultList = em.createQuery("select cr from ClubRole cr where cr.isDefault = false", ClubRole.class).getResultList();
            // then
            assertAll(
                    () -> assertThat(resultList.size()).isEqualTo(2),
                    () -> assertThat(resultList.stream().filter(it -> it.clubRoleCategory() == ClubRoleCategory.GENERAL)
                            .findAny().get().name()).isEqualTo(generalRoleName),
                    () -> assertThat(resultList.stream().filter(it -> it.clubRoleCategory() == ClubRoleCategory.OFFICER)
                            .findAny().get().name()).isEqualTo(officerRoleName)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("임원이나 회장이 아닌 일반 회원인 경우 새로운 역할을 생성할 권한이 없다.")
        void fail_test_1() {
            // given
            final String generalRoleName = "새로생성한 일반회원역할";
            final String officerRoleName = "새로생성한 임원역할";

            Member requester = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            ClubRole role = clubRoleCategoryClubRoleMap.get(ClubRoleCategory.GENERAL);
            Participant participant = saveParticipant(requester, club, role);

            // when
            BaseExceptionType type1 = assertThrows(ClubRoleException.class, () ->
                    createClubRoleUseCase.command(
                            new CreateClubRoleUseCase.Command(
                                    requester.id(),
                                    club.id(),
                                    generalRoleName,
                                    ClubRoleCategory.GENERAL
                            )
                    )).exceptionType();
            assertThat(type1).isEqualTo(NO_AUTHORITY_CREATE_ROLE);

            BaseExceptionType type2 = assertThrows(ClubRoleException.class, () ->
                    createClubRoleUseCase.command(
                            new CreateClubRoleUseCase.Command(
                                    requester.id(),
                                    club.id(),
                                    officerRoleName,
                                    ClubRoleCategory.OFFICER
                            )
                    )).exceptionType();
            assertThat(type2).isEqualTo(NO_AUTHORITY_CREATE_ROLE);
        }

        @ParameterizedTest(name = "[{arguments}] 어떠한 회원도 회장 역할은 새로 생성할 수 없다.")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"GENERAL"})
        void fail_test_2(final ClubRoleCategory category) {
            // given
            final String roleName = "새로생성한 회장역할";

            Member requester = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            ClubRole role = clubRoleCategoryClubRoleMap.get(category);
            Participant participant = saveParticipant(requester, club, role);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    createClubRoleUseCase.command(
                            new CreateClubRoleUseCase.Command(
                                    requester.id(),
                                    club.id(),
                                    roleName,
                                    ClubRoleCategory.PRESIDENT
                            )
                    )).exceptionType();
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE);
        }
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