package com.mohaeng.participant.application.service;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.application.usecase.ChangeTargetClubRoleUseCase;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.generalRole;
import static com.mohaeng.common.fixtures.ClubRoleFixture.officerRole;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static com.mohaeng.participant.exception.ParticipantExceptionType.*;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationTest
@DisplayName("ChangeTargetClubRole 는 ")
class ChangeTargetClubRoleTest {

    @Autowired
    private ChangeTargetClubRoleUseCase changeTargetClubRoleUseCase;

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

        @ParameterizedTest(name = """
                임원 혹은 회장은, 자신보다 낮은 계급의 회원의 역할을 변경할 수 있다.
                이때 회장의 역할로는 변경할 수 없다.
                """)
        @CsvSource({
                "PRESIDENT, OFFICER, GENERAL",
                "PRESIDENT, OFFICER, OFFICER",
                "PRESIDENT, GENERAL, GENERAL",
                "PRESIDENT, GENERAL, OFFICER",
                "OFFICER, GENERAL, GENERAL",
                "OFFICER, GENERAL, OFFICER",
        })
        void success_test_1(final ClubRoleCategory requesterRoleCategory,
                            final ClubRoleCategory targetRoleCategory,
                            final ClubRoleCategory changedRoleCategory) {
            // given
            Member member = saveMember();
            Member target = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Map<ClubRoleCategory, ClubRole> otherRoles = clubRoleRepository.saveAll(
                            List.of(generalRole("1", club),
                                    officerRole("2", club))
                    )
                    .stream()
                    .collect(toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            Participant requester = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(requesterRoleCategory));
            Participant targetParticipant = saveParticipant(target, club, clubRoleCategoryClubRoleMap.get(targetRoleCategory));

            em.flush();
            em.clear();

            // when
            changeTargetClubRoleUseCase.command(
                    new ChangeTargetClubRoleUseCase.Command(
                            member.id(),
                            targetParticipant.id(),
                            otherRoles.get(changedRoleCategory).id()
                    )
            );

            em.flush();
            em.clear();

            // then
            assertThat(participantRepository.findWithClubAndClubRoleById(targetParticipant.id()).get().clubRole().id())
                    .isEqualTo(otherRoles.get(changedRoleCategory).id());
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("일반 회원이 변경하려는 경우 예외가 발생")
        void fail_test_1() {
            // given
            Member member = saveMember();
            Member target = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant requester = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            Participant targetParticipant = saveParticipant(target, club, clubRoleCategoryClubRoleMap.get(GENERAL));

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    changeTargetClubRoleUseCase.command(
                            new ChangeTargetClubRoleUseCase.Command(
                                    member.id(),
                                    targetParticipant.id(),
                                    clubRoleCategoryClubRoleMap.get(OFFICER).id()
                            )
                    )).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_TARGET_ROLE);
            assertThat(participantRepository.findWithClubAndClubRoleById(targetParticipant.id()).get().clubRole().id())
                    .isEqualTo(clubRoleCategoryClubRoleMap.get(GENERAL).id());
        }

        @Test
        @DisplayName("바꾸려는 역할이 다른 모임의 역할인 경우 예외")
        void fail_test_2() {
            // given
            Member member = saveMember();
            Member target = saveMember();
            Club club = saveClub();
            Club other = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap1 = saveDefaultClubRoles(club);
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap2 = saveDefaultClubRoles(other);

            Participant requester = saveParticipant(member, club, clubRoleCategoryClubRoleMap1.get(PRESIDENT));
            Participant targetParticipant = saveParticipant(target, club, clubRoleCategoryClubRoleMap1.get(GENERAL));

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    changeTargetClubRoleUseCase.command(
                            new ChangeTargetClubRoleUseCase.Command(
                                    member.id(),
                                    targetParticipant.id(),
                                    clubRoleCategoryClubRoleMap2.get(OFFICER).id()
                            )
                    )).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_CHANGE_TO_OTHER_CLUB_ROLE);
            assertThat(participantRepository.findWithClubAndClubRoleById(targetParticipant.id()).get().clubRole().id())
                    .isEqualTo(clubRoleCategoryClubRoleMap1.get(GENERAL).id());
        }

        @Test
        @DisplayName("바꾸려는 회원이 다른 모임의 회원 경우 예외 (참여자를 찾을 수 없다는 예외)")
        void fail_test_3() {
            // given
            Member member = saveMember();
            Member target = saveMember();
            Club club = saveClub();
            Club other = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap1 = saveDefaultClubRoles(club);
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap2 = saveDefaultClubRoles(other);

            Participant requester = saveParticipant(member, club, clubRoleCategoryClubRoleMap1.get(PRESIDENT));
            Participant targetParticipant = saveParticipant(target, other, clubRoleCategoryClubRoleMap2.get(GENERAL));

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    changeTargetClubRoleUseCase.command(
                            new ChangeTargetClubRoleUseCase.Command(
                                    member.id(),
                                    targetParticipant.id(),
                                    clubRoleCategoryClubRoleMap2.get(OFFICER).id()
                            )
                    )).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
            assertThat(participantRepository.findWithClubAndClubRoleById(targetParticipant.id()).get().clubRole().id())
                    .isEqualTo(clubRoleCategoryClubRoleMap2.get(GENERAL).id());
        }

        @Test
        @DisplayName("회장으로 변경하려는 경우 예외")
        void fail_test_4() {
            // given
            Member member = saveMember();
            Member target = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant requester = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            Participant targetParticipant = saveParticipant(target, club, clubRoleCategoryClubRoleMap.get(GENERAL));

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    changeTargetClubRoleUseCase.command(
                            new ChangeTargetClubRoleUseCase.Command(
                                    member.id(),
                                    targetParticipant.id(),
                                    clubRoleCategoryClubRoleMap.get(PRESIDENT).id()
                            )
                    )).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_CHANGED_TO_PRESIDENT_ROLE);
            assertThat(participantRepository.findWithClubAndClubRoleById(targetParticipant.id()).get().clubRole().id())
                    .isEqualTo(clubRoleCategoryClubRoleMap.get(GENERAL).id());
        }

        @ParameterizedTest(name = " 자신과 계급이 동일하거나, 자신보다 높은 계급의 회원의 역할을 변경하려는 경우 예외")
        @CsvSource({
                "OFFICER, OFFICER, GENERAL",
                "OFFICER, OFFICER, OFFICER",
                "OFFICER, PRESIDENT, GENERAL",
                "OFFICER, PRESIDENT, OFFICER",
        })
        void fail_test_5(final ClubRoleCategory requesterRoleCategory,
                         final ClubRoleCategory targetRoleCategory,
                         final ClubRoleCategory changedRoleCategory) {
            // given
            Member member = saveMember();
            Member target = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Map<ClubRoleCategory, ClubRole> otherRoles = clubRoleRepository.saveAll(
                            List.of(generalRole("1", club),
                                    officerRole("2", club))
                    )
                    .stream()
                    .collect(toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            Participant requester = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(requesterRoleCategory));
            Participant targetParticipant = saveParticipant(target, club, clubRoleCategoryClubRoleMap.get(targetRoleCategory));

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    changeTargetClubRoleUseCase.command(
                            new ChangeTargetClubRoleUseCase.Command(
                                    member.id(),
                                    targetParticipant.id(),
                                    otherRoles.get(changedRoleCategory).id()
                            )
                    )).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_TARGET_ROLE);
            assertThat(participantRepository.findWithClubAndClubRoleById(targetParticipant.id()).get().clubRole().id())
                    .isEqualTo(clubRoleCategoryClubRoleMap.get(targetRoleCategory).id());
        }
    }

    private Participant saveParticipant(final Member member, final Club club, final ClubRole clubRole) {
        return participantRepository.save(participant(null, member, club, clubRole));
    }

    private Map<ClubRoleCategory, ClubRole> saveDefaultClubRoles(final Club club) {
        return clubRoleRepository.saveAll(ClubRole.defaultRoles(club))
                .stream()
                .collect(toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));
    }

    private Club saveClub() {
        return clubRepository.save(club(null));
    }

    private Member saveMember() {
        return memberRepository.save(member(null));
    }
}