package com.mohaeng.participant.application.service;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.application.usecase.LeaveParticipantUseCase;
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

import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_EMPTY;
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
@DisplayName("LeaveParticipant 는 ")
class LeaveParticipantTest {

    @Autowired
    private LeaveParticipant leaveParticipant;

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

        @ParameterizedTest(name = "회원(CLUB_ROLE = {arguments})을 모임에서 탈퇴시키고, 모임의 회원 수는 1 감소한다.")
        @EnumSource(mode = EXCLUDE, names = {"PRESIDENT"})
        void success_test_1(final ClubRoleCategory clubRoleCategory) {
            // given
            Member member = saveMember();
            Member member2 = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(clubRoleCategory));
            saveParticipant(member2, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            leaveParticipant.command(
                    new LeaveParticipantUseCase.Command(member.id(), participant.id())
            );

            // then
            assertAll(
                    () -> assertThat(participantRepository.findById(participant.id())).isEmpty(),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount - 1)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {
        @Test
        @DisplayName("회원이 모임에 존재하지 않는 경우 예외를 발생시킨다.")
        void fail_test_1() {
            // given
            Member member = saveMember();
            Club club = saveClub();
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    leaveParticipant.command(
                            new LeaveParticipantUseCase.Command(member.id(), 1L)
                    )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
        }

        @Test
        @DisplayName("회원이 존재하지 않는 경우 예외를 발생시킨다.")
        void fail_test_2() {
            // given
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    leaveParticipant.command(
                            new LeaveParticipantUseCase.Command(100L, participant.id())
                    )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_LEAVE_PARTICIPANT_REQUEST),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
        }

        @Test
        @DisplayName("참여자(Participant)의 Member ID와 요청자의 MemberId가 일치하지 않으면 오류가 발생한다.")
        void fail_test_3() {
            // given
            Member member = saveMember();
            Member member2 = saveMember();
            Member other = saveMember();

            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);

            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            saveParticipant(member2, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));

            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    leaveParticipant.command(
                            new LeaveParticipantUseCase.Command(other.id(), participant.id())
                    )).exceptionType();
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_LEAVE_PARTICIPANT_REQUEST),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
        }

        @Test
        @DisplayName("회장은 모임에서 탈퇴할 수 없다")
        void fail_test_4() {
            // given
            Member member = saveMember();
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            Participant president = saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    leaveParticipant.command(
                            new LeaveParticipantUseCase.Command(presidentMember.id(), president.id())
                    )).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(PRESIDENT_CAN_NOT_LEAVE_CLUB),
                    () -> assertThat(participantRepository.findById(president.id())).isNotEmpty(),
                    () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
            );
        }

        // TODO : 테스트에 존재하는 @Transactional로 묶여서, 롤백이 정상적으로 수행되는지 확인할 수 없다. 어카징.. (일단 코드 순서 재배치를 통해 해결)
        @Test
        @DisplayName("모임의 인원이 1명인 경우 탈퇴할 수 없다. (회장은 탈퇴할 수 없으므로, 일반적으로 회장이 탈퇴하는 요청을 보낸 게 아닌 이상 발생해서는 안되는 케이스이다.)")
        void fail_test_5() {
            // given
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(GENERAL));
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                    leaveParticipant.command(
                            new LeaveParticipantUseCase.Command(member.id(), participant.id())
                    )).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(CLUB_IS_EMPTY),
                    () -> assertThat(participantRepository.findById(participant.id())).isNotEmpty(),
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