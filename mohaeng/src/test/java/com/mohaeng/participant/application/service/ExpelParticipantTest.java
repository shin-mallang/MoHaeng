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
import com.mohaeng.participant.application.usecase.ExpelParticipantUseCase;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static com.mohaeng.participant.exception.ParticipantExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@ApplicationTest
@DisplayName("ExpelParticipant 는 ")
class ExpelParticipantTest {

    @Autowired
    private ExpelParticipant expelParticipant;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @ParameterizedTest(name = "회원(CLUB_ROLE = {arguments})을 모임에서 추방시키고, 모임의 회원 수는 1 감소한다.")
        @EnumSource(mode = EXCLUDE, names = {"PRESIDENT"})
        void success_test_1(final ClubRoleCategory clubRoleCategory) {
            // given
            Member member = saveMember();
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant expelTargetParticipant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(clubRoleCategory));
            Participant president = saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            expelParticipant.command(
                    new ExpelParticipantUseCase.Command(presidentMember.id(), expelTargetParticipant.id())
            );

            // then
            assertAll(
                    () -> assertThat(participantRepository.findById(expelTargetParticipant.id())).isEmpty(),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount - 1)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {
        @Test
        @DisplayName("추방하려는 회원이 모임에 존재하지 않는 경우 예외를 발생시킨다.")
        void fail_test_1() {
            // given
            Member member = saveMember();
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant expelTargetParticipant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            Participant president = saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    expelParticipant.command(
                            new ExpelParticipantUseCase.Command(presidentMember.id(), expelTargetParticipant.id() + 10L)
                    )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
        }

        @DisplayName("회장이 아닌 경우 추방할 수 없다.")
        @ParameterizedTest(name = "회장이 아닌 경우(ex: {arguments}) 참여자를 추방할 수 없다.")
        @EnumSource(mode = EXCLUDE, names = {"PRESIDENT"})
        void fail_test_2(final ClubRoleCategory clubRoleCategory) {
            // given
            Member member = saveMember();
            Member requesterMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant expelTargetParticipant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            Participant requester = saveParticipant(requesterMember, club, clubRoleCategoryClubRoleMap.get(clubRoleCategory));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    expelParticipant.command(
                            new ExpelParticipantUseCase.Command(requesterMember.id(), expelTargetParticipant.id())
                    )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
        }

        @Test
        @DisplayName("회장(Participant)의 Member ID와 요청자의 MemberId가 일치하지 않으면 오류가 발생한다.")
        void fail_test_3() {
            // given
            Member member = saveMember();
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant expelTargetParticipant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            Participant president = saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    expelParticipant.command(
                            new ExpelParticipantUseCase.Command(presidentMember.id() + 1L, expelTargetParticipant.id())
                    )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(MISMATCH_BETWEEN_PARTICIPANT_AND_MEMBER),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
        }

        @Test
        @DisplayName("모임에 회장이 없으면 예외를 발생시킨다. (회장은 반드시 존재하므로 발생하지 않을 예외)")
        void fail_test_4() {
            // given
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant expelTargetParticipant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    expelParticipant.command(
                            new ExpelParticipantUseCase.Command(member.id(), expelTargetParticipant.id())
                    )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PRESIDENT),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
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