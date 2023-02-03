package com.mohaeng.participant.application.service;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.application.usecase.DeleteClubRoleUseCase;
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

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.*;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NO_AUTHORITY_DELETE_ROLE;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.generalRole;
import static com.mohaeng.common.fixtures.ClubRoleFixture.officerRole;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

@ApplicationTest
@DisplayName("DeleteClubRole 은 ")
class DeleteClubRoleTest {

    @Autowired
    private DeleteClubRoleUseCase deleteClubRoleUseCase;

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

        @ParameterizedTest(name = "[{arguments}] 회장과 임원만이 역할을 제거할 수 있다.")
        @EnumSource(mode = INCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void success_test_1(final ClubRoleCategory category) {
            // given
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(
                    List.of(generalRole("제거할 일반", club), officerRole("제거할 임원", club))
            );
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(category));

            // when
            clubRoles.forEach(it -> {
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                member.id(),
                                it.id()
                        )
                );
            });

            // then
            clubRoles.forEach(it ->
                    assertThat(clubRoleRepository.findById(it.id())).isEmpty()
            );
        }

        @Test
        @DisplayName("기본 역할을 제거하는 경우, 해당 범주에 속하는 임의의 역할이 기본 역할이 된다.")
        void success_test_2() {
            Member member = saveMember();
            Club club = saveClub();
            // 기본 역할
            Map<ClubRoleCategory, ClubRole> defaultRoleMap = saveDefaultClubRoles(club);

            // 추가 역할
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(
                    List.of(generalRole("추가한 일반", club), officerRole("추가한 임원", club))
            );
            Participant participant = saveParticipant(member, club, defaultRoleMap.get(PRESIDENT));

            assertAll(
                    () -> assertThat(defaultRoleMap.get(PRESIDENT).isDefault()).isTrue(),
                    () -> assertThat(defaultRoleMap.get(OFFICER).isDefault()).isTrue(),
                    () -> assertThat(defaultRoleMap.get(GENERAL).isDefault()).isTrue()
            );

            // when
            defaultRoleMap.keySet()
                    .stream()
                    .filter(it -> it != PRESIDENT)
                    .map(defaultRoleMap::get)
                    .forEach(it -> {
                        deleteClubRoleUseCase.command(
                                new DeleteClubRoleUseCase.Command(
                                        member.id(),
                                        it.id()
                                )
                        );
                    });

            // then
            defaultRoleMap.keySet()
                    .stream()
                    .filter(it -> it != PRESIDENT)
                    .map(defaultRoleMap::get)
                    .forEach(it ->
                            assertThat(clubRoleRepository.findById(it.id())).isEmpty()
                    );
            // 임의의 역할이 기본 역할로 변경 테스트
            clubRoles.forEach(it ->
                    assertThat(clubRoleRepository.findById(it.id()).get().isDefault()).isTrue()
            );
        }

        @Test
        @DisplayName("기존 제거된 역할을 부여받은 회원은, 해당 범주의 기본 역할로 역할이 변경된다.")
        void success_test_3() {
            Member member = saveMember();
            Club club = saveClub();
            // 기본 역할
            Map<ClubRoleCategory, ClubRole> defaultRoleMap = saveDefaultClubRoles(club);

            // 추가 역할
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(
                    List.of(generalRole("추가한 일반", club), officerRole("추가한 임원", club))
            );
            saveParticipant(member, club, defaultRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, clubRoles.get(0));
            saveParticipant(saveMember(), club, clubRoles.get(0));
            saveParticipant(saveMember(), club, clubRoles.get(1));
            saveParticipant(saveMember(), club, clubRoles.get(1));

            // when
            clubRoles.forEach(it -> {
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                member.id(),
                                it.id()
                        )
                );
            });

            // then
            clubRoles.forEach(it ->
                    assertThat(clubRoleRepository.findById(it.id())).isEmpty()
            );

            // 기존 참여자 역할 변경 테스트
            participantRepository.findAllWithMemberByClubId(club.id())
                    .forEach(it ->
                            assertThat(it.clubRole()).isEqualTo(defaultRoleMap.get(it.clubRole().clubRoleCategory()))
                    );
        }

        @Test
        @DisplayName("기본 역할을 제거하는 경우, 해당 범주에 속하는 임의의 역할이 기본 역할이 된며, 기존 제거된 역할을 부여받은 회원은 해당 역할로 역할이 변경된다.")
        void success_test_4() {
            Member member = saveMember();
            Club club = saveClub();
            // 기본 역할
            Map<ClubRoleCategory, ClubRole> defaultRoleMap = saveDefaultClubRoles(club);

            // 추가 역할
            ClubRole generalRole = generalRole("추가한 일반", club);
            ClubRole officerRole = officerRole("추가한 임원", club);
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(
                    List.of(generalRole, officerRole)
            );
            Participant participant = saveParticipant(member, club, defaultRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, defaultRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, defaultRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, defaultRoleMap.get(GENERAL));
            saveParticipant(saveMember(), club, defaultRoleMap.get(GENERAL));

            assertAll(
                    () -> assertThat(defaultRoleMap.get(PRESIDENT).isDefault()).isTrue(),
                    () -> assertThat(defaultRoleMap.get(OFFICER).isDefault()).isTrue(),
                    () -> assertThat(defaultRoleMap.get(GENERAL).isDefault()).isTrue()
            );

            // when
            defaultRoleMap.keySet()
                    .stream()
                    .filter(it -> it != PRESIDENT)
                    .map(defaultRoleMap::get)
                    .forEach(it -> {
                        deleteClubRoleUseCase.command(
                                new DeleteClubRoleUseCase.Command(
                                        member.id(),
                                        it.id()
                                )
                        );
                    });

            // then
            defaultRoleMap.keySet()
                    .stream()
                    .filter(it -> it != PRESIDENT)
                    .map(defaultRoleMap::get)
                    .forEach(it ->
                            assertThat(clubRoleRepository.findById(it.id())).isEmpty()
                    );

            clubRoles.forEach(it ->
                    assertThat(clubRoleRepository.findById(it.id()).get().isDefault()).isTrue()
            );

            // 기존 참여자 역할 변경 테스트
            for (Participant changeRoleParticipant : participantRepository.findAllWithMemberByClubId(club.id())) {
                if (changeRoleParticipant.clubRole().clubRoleCategory() == OFFICER) {
                    assertThat(changeRoleParticipant.clubRole()).isEqualTo(officerRole);
                    continue;
                }
                if (changeRoleParticipant.clubRole().clubRoleCategory() == GENERAL) {
                    assertThat(changeRoleParticipant.clubRole()).isEqualTo(generalRole);
                }
            }
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @ParameterizedTest(name = "[{arguments}] 회장과 임원만이 역할을 제거할 수 있다.")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void fail_test_1(final ClubRoleCategory category) {
            // given
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(
                    List.of(generalRole("제거할 일반", club), officerRole("제거할 임원", club))
            );
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(category));

            // when
            clubRoles.forEach(it -> {
                BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                        deleteClubRoleUseCase.command(
                                new DeleteClubRoleUseCase.Command(
                                        member.id(),
                                        it.id()
                                )
                        )).exceptionType();
                assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELETE_ROLE);
            });

            // then
            clubRoles.forEach(it ->
                    assertThat(clubRoleRepository.findById(it.id())).isNotEmpty()
            );
        }

        @Test
        @DisplayName("역할을 제거하였을 때, 해당 범주에 남아있는 역할이 존재하지 않으면 해당 역할은 제거할 수 없다.")
        void fail_test_2() {
            Member member = saveMember();
            Club club = saveClub();
            // 기본 역할
            Map<ClubRoleCategory, ClubRole> defaultRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, defaultRoleMap.get(PRESIDENT));

            assertAll(
                    () -> assertThat(defaultRoleMap.get(PRESIDENT).isDefault()).isTrue(),
                    () -> assertThat(defaultRoleMap.get(OFFICER).isDefault()).isTrue(),
                    () -> assertThat(defaultRoleMap.get(GENERAL).isDefault()).isTrue()
            );

            // when
            defaultRoleMap.keySet()
                    .stream()
                    .filter(it -> it != PRESIDENT)
                    .map(defaultRoleMap::get)
                    .forEach(it -> {
                        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                                deleteClubRoleUseCase.command(
                                        new DeleteClubRoleUseCase.Command(
                                                member.id(),
                                                it.id()
                                        )
                                )
                        ).exceptionType();
                        assertThat(baseExceptionType).isEqualTo(CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE);
                    });

            // then
            defaultRoleMap.keySet()
                    .stream()
                    .filter(it -> it != PRESIDENT)
                    .map(defaultRoleMap::get)
                    .forEach(it ->
                            assertThat(clubRoleRepository.findById(it.id())).isNotEmpty()
                    );
        }

        @Test
        @DisplayName("회장 역할을 제거하려는 경우 예외가 발생한다.")
        void fail_test_3() {
            // given
            Member member = saveMember();
            Club club = saveClub();
            // 기본 역할
            Map<ClubRoleCategory, ClubRole> defaultRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, defaultRoleMap.get(PRESIDENT));

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    deleteClubRoleUseCase.command(
                            new DeleteClubRoleUseCase.Command(
                                    member.id(),
                                    defaultRoleMap.get(PRESIDENT).id()
                            )
                    )
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE);
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