package com.mohaeng.participant.domain.model;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_EMPTY;
import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.*;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.*;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.*;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT;
import static com.mohaeng.participant.exception.ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Participant 는 ")
class ParticipantTest {

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("joinClub() 시 모임에 가입한다.")
        void success_test_1() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole clubRole = presidentRole("회장", club);
            Participant participant = new Participant(member);
            int currentParticipantCount = club.currentParticipantCount();

            // when
            participant.joinClub(club, clubRole);

            // then
            assertAll(
                    () -> assertThat(participant.club()).isEqualTo(club),
                    () -> assertThat(participant.clubRole()).isEqualTo(clubRole),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount + 1)
            );
        }

        @Test
        @DisplayName("isManager() 시 회장 혹은 임원인 경우 true를 반환한다.")
        void success_test_2() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);

            Participant president = participant(null, member, club, presidentRole);
            Participant officer = participant(null, member, club, officerRole);
            Participant general = participant(null, member, club, generalRole);

            // when, then
            assertAll(
                    () -> assertThat(president.isManager()).isTrue(),
                    () -> assertThat(officer.isManager()).isTrue(),
                    () -> assertThat(general.isManager()).isFalse()
            );
        }

        @Test
        @DisplayName("leaveFromClub() 시 모임의 인원 수를 1 감소시킨다.")
        void success_test_3() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole clubRole = officerRole("회장", club);
            Participant participant = participant(null, member, club, clubRole);
            club.participantCountUp();
            club.participantCountUp();
            int currentParticipantCount = club.currentParticipantCount();

            // when
            participant.leaveFromClub();

            // then
            assertAll(
                    () -> assertThat(participant.club()).isNull(),
                    () -> assertThat(participant.clubRole()).isNull(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount - 1)
            );
        }

        @Test
        @DisplayName("expelFromClub() 시 모임의 인원 수를 1 감소시킨다.")
        void success_test_4() {
            // given
            Member member = member(null);
            Member officerMember = member(null);
            Club club = club(null);
            ClubRole clubRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            Participant participant = participant(null, member, club, clubRole);
            Participant target = participant(null, officerMember, club, officerRole);
            club.participantCountUp();
            club.participantCountUp();
            int currentParticipantCount = club.currentParticipantCount();

            // when
            participant.expelFromClub(target);

            // then
            assertAll(
                    () -> assertThat(target.club()).isNull(),
                    () -> assertThat(target.clubRole()).isNull(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount - 1)
            );
        }

        @Test
        @DisplayName("createClubRole() 시 임원 혹은 회장인 경우 ClubRole을 생성한다.")
        void success_test_5() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            Participant president = participant(null, member, club, presidentRole);
            Participant officer = participant(null, member, club, officerRole);

            // when
            ClubRole general1 = president.createClubRole("생성", GENERAL);
            ClubRole general2 = officer.createClubRole("생성", GENERAL);
            ClubRole officer1 = president.createClubRole("생성", OFFICER);
            ClubRole officer2 = officer.createClubRole("생성", OFFICER);

            // then
            assertAll(
                    () -> assertThat(general1.name()).isEqualTo("생성"),
                    () -> assertThat(general1.clubRoleCategory()).isEqualTo(GENERAL),
                    () -> assertThat(officer1.name()).isEqualTo("생성"),
                    () -> assertThat(officer1.clubRoleCategory()).isEqualTo(OFFICER),
                    () -> assertThat(general2.name()).isEqualTo("생성"),
                    () -> assertThat(general2.clubRoleCategory()).isEqualTo(GENERAL),
                    () -> assertThat(officer2.name()).isEqualTo("생성"),
                    () -> assertThat(officer2.clubRoleCategory()).isEqualTo(OFFICER)
            );
        }

        @Test
        @DisplayName("changeClubRoleName() 시 임원 혹은 회장인 경우 ClubRole의 이름을 변경한다.")
        void success_test_6() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);
            Participant president = participant(null, member, club, presidentRole);
            Participant officer = participant(null, member, club, officerRole);

            String changeName1 = "변경1!@#";
            String changeName2 = "변경2!@#";

            // when (회장이 변경)
            president.changeClubRoleName(presidentRole, changeName1);
            president.changeClubRoleName(officerRole, changeName1);
            president.changeClubRoleName(generalRole, changeName1);

            // then
            assertAll(
                    () -> assertThat(presidentRole.name()).isEqualTo(changeName1),
                    () -> assertThat(officerRole.name()).isEqualTo(changeName1),
                    () -> assertThat(generalRole.name()).isEqualTo(changeName1)
            );

            // when (임원이 변경)
            officer.changeClubRoleName(presidentRole, changeName2);
            officer.changeClubRoleName(officerRole, changeName2);
            officer.changeClubRoleName(generalRole, changeName2);

            assertAll(
                    () -> assertThat(presidentRole.name()).isEqualTo(changeName2),
                    () -> assertThat(officerRole.name()).isEqualTo(changeName2),
                    () -> assertThat(generalRole.name()).isEqualTo(changeName2)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("joinClub() 시 모임이 가득 찼다면 예외가 발생한다.")
        void fail_test_1() {
            // given
            Member member = member(null);
            Club club = new Club("name", "des", 1);
            club.participantCountUp();  // 모임 가득 채우기
            ClubRole clubRole = presidentRole("회장", club);
            Participant participant = new Participant(member);
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                    participant.joinClub(club, clubRole))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(participant.club()).isNull(),
                    () -> assertThat(participant.clubRole()).isNull(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount),
                    () -> assertThat(baseExceptionType).isEqualTo(CLUB_IS_FULL)
            );
        }

        @Test
        @DisplayName("leaveFromClub() 시 모임의 기존 인원이 1명인 경우 예외가 발생한다.")
        void fail_test_2() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole clubRole = officerRole("임원", club);
            Participant participant = participant(null, member, club, clubRole);
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class,
                    participant::leaveFromClub)
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(participant.club()).isNotNull(),
                    () -> assertThat(participant.clubRole()).isNotNull(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount),
                    () -> assertThat(baseExceptionType).isEqualTo(CLUB_IS_EMPTY)
            );
        }

        @Test
        @DisplayName("leaveFromClub() 시 탈퇴하려는 사람이 회장인 경우 예외가 발생한다.")
        void fail_test_3() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole clubRole = presidentRole("회장", club);
            Participant participant = participant(null, member, club, clubRole);
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class,
                    participant::leaveFromClub)
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(participant.club()).isNotNull(),
                    () -> assertThat(participant.clubRole()).isNotNull(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount),
                    () -> assertThat(baseExceptionType).isEqualTo(PRESIDENT_CAN_NOT_LEAVE_CLUB)
            );
        }

        @Test
        @DisplayName("expelFromClub() 시 모임의 기존 인원이 1명인 경우 예외가 발생한다. (발생하는 경우는 없다.)")
        void fail_test_4() {
            // given
            Member member = member(null);
            Member officerMember = member(null);
            Club club = club(null);
            ClubRole clubRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            Participant participant = participant(null, member, club, clubRole);
            Participant target = participant(null, officerMember, club, officerRole);
            club.participantCountDown();
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class,
                    () -> participant.expelFromClub(target))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(target.club()).isNotNull(),
                    () -> assertThat(target.clubRole()).isNotNull(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount),
                    () -> assertThat(baseExceptionType).isEqualTo(CLUB_IS_EMPTY)
            );
        }

        @Test
        @DisplayName("expelFromClub() 시 추방하는 사람이 회장이 아닌 경우 예외가 발생한다.")
        void fail_test_5() {
            // given
            Member member = member(null);
            Member officerMember = member(null);
            Club club = club(null);
            ClubRole officerRole = officerRole("임원", club);
            Participant participant = participant(null, member, club, officerRole);
            Participant target = participant(null, officerMember, club, officerRole);
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class,
                    () -> participant.expelFromClub(target))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(target.club()).isNotNull(),
                    () -> assertThat(target.clubRole()).isNotNull(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(currentParticipantCount),
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT)
            );
        }

        @Test
        @DisplayName("createClubRole() 시 임원 혹은 회장이 아닌경우 예외가 발생한다.")
        void fail_test_6() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole clubRole = generalRole("일반", club);
            Participant participant = participant(null, member, club, clubRole);

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ClubRoleException.class,
                    () -> participant.createClubRole("생성", GENERAL))
                    .exceptionType();

            BaseExceptionType baseExceptionType2 = assertThrows(ClubRoleException.class,
                    () -> participant.createClubRole("생성", OFFICER))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_CREATE_ROLE),
                    () -> assertThat(baseExceptionType2).isEqualTo(NO_AUTHORITY_CREATE_ROLE)
            );
        }

        @Test
        @DisplayName("createClubRole() 시 회장 역할을 생성하는 경우 예외가 발생한다.")
        void fail_test_7() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            Participant president = participant(null, member, club, presidentRole);
            Participant officer = participant(null, member, club, officerRole);

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ClubRoleException.class,
                    () -> president.createClubRole("생성", PRESIDENT))
                    .exceptionType();

            BaseExceptionType baseExceptionType2 = assertThrows(ClubRoleException.class,
                    () -> officer.createClubRole("생성", PRESIDENT))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType1).isEqualTo(CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE),
                    () -> assertThat(baseExceptionType2).isEqualTo(CAN_NOT_CREATE_ADDITIONAL_PRESIDENT_ROLE)
            );
        }

        @Test
        @DisplayName("changeClubRoleName() 시 임원 혹은 회장이 아닌 경우 예외가 발생한다.")
        void fail_test_8() {
            // given
            Member member = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);
            Participant participant = participant(null, member, club, generalRole);

            String changeName = "변경1!@#";

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ClubRoleException.class,
                    () -> participant.changeClubRoleName(presidentRole, changeName))
                    .exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ClubRoleException.class,
                    () -> participant.changeClubRoleName(officerRole, changeName))
                    .exceptionType();
            BaseExceptionType baseExceptionType3 = assertThrows(ClubRoleException.class,
                    () -> participant.changeClubRoleName(generalRole, changeName))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME),
                    () -> assertThat(baseExceptionType2).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME),
                    () -> assertThat(baseExceptionType3).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME)
            );
        }
    }
}