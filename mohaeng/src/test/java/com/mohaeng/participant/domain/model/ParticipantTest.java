package com.mohaeng.participant.domain.model;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.exception.ParticipantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED_APPLICATION_FORM;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM;
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
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

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

        @Test
        @DisplayName("approveApplicationForm() 시 임원 혹은 회장인 경우 가입 신청서를 처리상태로 만든 뒤, 모임에 가입된 Participant를 반환한다.")
        void success_test_7() {
            // given
            Member member = member(null);
            Member applicant = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);
            Participant president = participant(null, member, club, presidentRole);
            Participant officer = participant(null, member, club, officerRole);

            ApplicationForm applicationForm1 = ApplicationForm.create(applicant, club);
            ApplicationForm applicationForm2 = ApplicationForm.create(applicant, club);

            // when
            Participant participant1 = president.approveApplicationForm(applicationForm1, generalRole);
            Participant participant2 = officer.approveApplicationForm(applicationForm2, generalRole);

            // then
            assertAll(
                    () -> assertThat(participant1.club()).isEqualTo(club),
                    () -> assertThat(participant1.clubRole()).isEqualTo(generalRole),
                    () -> assertThat(applicationForm1.processed()).isTrue(),
                    () -> assertThat(participant2.club()).isEqualTo(club),
                    () -> assertThat(participant2.clubRole()).isEqualTo(generalRole),
                    () -> assertThat(applicationForm2.processed()).isTrue()
            );
        }

        @Test
        @DisplayName("rejectApplicationForm() 시 임원 혹은 회장인 경우 가입 신청서를 처리상태로 만든다.")
        void success_test_8() {
            // given
            Member member = member(null);
            Member applicant = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            Participant president = participant(null, member, club, presidentRole);
            Participant officer = participant(null, member, club, officerRole);

            ApplicationForm applicationForm1 = ApplicationForm.create(applicant, club);
            ApplicationForm applicationForm2 = ApplicationForm.create(applicant, club);
            ;

            // when
            president.rejectApplicationForm(applicationForm1);
            officer.rejectApplicationForm(applicationForm2);

            // then
            assertAll(
                    () -> assertThat(applicationForm1.processed()).isTrue(),
                    () -> assertThat(applicationForm2.processed()).isTrue()
            );
        }

        @ParameterizedTest(name = "[{arguments}] deleteClubRole() 시 권한이 있다면(회장 혹은 임원) 해당 역할을 기본 역할이 아니도록 만든다. ")
        @EnumSource(mode = INCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void success_test_9(final ClubRoleCategory category) {
            // given
            Member member = member(null);
            Club club = club(null);
            Map<ClubRoleCategory, ClubRole> defaultRoles = ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);

            Participant manager = participant(null, member, club, defaultRoles.get(category));

            // when
            manager.deleteClubRole(officerRole);
            manager.deleteClubRole(generalRole);

            // then
            assertAll(
                    () -> assertThat(officerRole.isDefault()).isFalse(),
                    () -> assertThat(generalRole.isDefault()).isFalse()
            );
        }

        @ParameterizedTest(name = "[{arguments}] changeDefaultRole() 시 권한이 있다면(회장 혹은 임원) 첫번째 Role은 기본 역할로 변경하고, 두번째 Role은 기본 역할이 아니도록 변경한다.")
        @EnumSource(mode = INCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void success_test_10(final ClubRoleCategory category) {
            // given
            Member member = member(null);
            Club club = club(null);
            Map<ClubRoleCategory, ClubRole> defaultRoles = ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);

            Participant manager = participant(null, member, club, defaultRoles.get(category));

            // when
            manager.changeDefaultRole(officerRole, defaultRoles.get(OFFICER));
            manager.changeDefaultRole(generalRole, defaultRoles.get(GENERAL));

            // then
            assertAll(
                    () -> assertThat(officerRole.isDefault()).isTrue(),
                    () -> assertThat(defaultRoles.get(OFFICER).isDefault()).isFalse(),
                    () -> assertThat(generalRole.isDefault()).isTrue(),
                    () -> assertThat(defaultRoles.get(GENERAL).isDefault()).isFalse()
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

        @Test
        @DisplayName("approveApplicationForm() 혹은 rejectApplicationForm() 시 이미 처리된 가입 신청서인 경우 예외가 발생한다.")
        void fail_test_9() {
            // given
            Member member = member(null);
            Member applicant = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);
            Participant president = participant(null, member, club, presidentRole);

            ApplicationForm applicationForm1 = ApplicationForm.create(applicant, club);
            applicationForm1.process();
            ApplicationForm applicationForm2 = ApplicationForm.create(applicant, club);
            applicationForm2.process();

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ApplicationFormException.class,
                    () -> president.approveApplicationForm(applicationForm1, generalRole))
                    .exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ApplicationFormException.class,
                    () -> president.rejectApplicationForm(applicationForm2))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType1).isEqualTo(ALREADY_PROCESSED_APPLICATION_FORM),
                    () -> assertThat(baseExceptionType2).isEqualTo(ALREADY_PROCESSED_APPLICATION_FORM)
            );
        }

        @Test
        @DisplayName("approveApplicationForm() 혹은 rejectApplicationForm() 시 임원이나 회장이 아닌 경우 예외가 발생한다.")
        void fail_test_10() {
            // given
            Member member = member(null);
            Member applicant = member(null);
            Club club = club(null);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);
            Participant general = participant(null, member, club, generalRole);

            ApplicationForm applicationForm1 = ApplicationForm.create(applicant, club);

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ApplicationFormException.class,
                    () -> general.approveApplicationForm(applicationForm1, generalRole))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION_FORM),
                    () -> assertThat(applicationForm1.processed()).isFalse()
            );
        }

        @Test
        @DisplayName("approveApplicationForm() 시 해당 모임이 가득 찬 경우 예외가 발생한다.")
        void fail_test_11() {
            // given
            Member member = member(null);
            Member applicant = member(null);
            Club club = new Club("name", "des", 1);
            ClubRole presidentRole = presidentRole("회장", club);
            ClubRole generalRole = generalRole("일반", club);
            Participant president = participant(null, member, club, presidentRole);

            ApplicationForm applicationForm1 = ApplicationForm.create(applicant, club);

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ClubException.class,
                    () -> president.approveApplicationForm(applicationForm1, generalRole))
                    .exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType1).isEqualTo(CLUB_IS_FULL)
            );
        }

        @ParameterizedTest(name = "[{arguments}] deleteClubRole() 시 권한이 없다면(회장 혹은 임원이 아닌 경우) 예외가 발생한다.")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void fail_test_12(final ClubRoleCategory category) {
            // given
            Member member = member(null);
            Member applicant = member(null);
            Club club = club(null);
            Map<ClubRoleCategory, ClubRole> defaultRoles = ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);

            Participant manager = participant(null, member, club, defaultRoles.get(category));

            // when & then
            assertThat(assertThrows(ClubRoleException.class, () ->
                    manager.deleteClubRole(officerRole))
                    .exceptionType())
                    .isEqualTo(NO_AUTHORITY_DELETE_ROLE);
            assertThat(assertThrows(ClubRoleException.class, () ->
                    manager.deleteClubRole(generalRole))
                    .exceptionType())
                    .isEqualTo(NO_AUTHORITY_DELETE_ROLE);
            // then
            assertAll(
                    () -> assertThat(officerRole.isDefault()).isFalse(),
                    () -> assertThat(generalRole.isDefault()).isFalse()
            );
        }

        @ParameterizedTest(name = "[{arguments}] changeDefaultRole() 시 권한이 없다면(회장 혹은 임원) 예외가 발생한다.")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void fail_test_13(final ClubRoleCategory category) {
            // given
            Member member = member(null);
            Club club = club(null);
            Map<ClubRoleCategory, ClubRole> defaultRoles = ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);

            Participant manager = participant(null, member, club, defaultRoles.get(category));

            // when
            assertThat(
                    assertThrows(ClubRoleException.class, () ->
                            manager.changeDefaultRole(officerRole, defaultRoles.get(OFFICER))
                    ).exceptionType()
            ).isEqualTo(NO_AUTHORITY_CHANGE_DEFAULT_ROLE);

            assertThat(
                    assertThrows(ClubRoleException.class, () ->
                            manager.changeDefaultRole(generalRole, defaultRoles.get(GENERAL))
                    ).exceptionType()
            ).isEqualTo(NO_AUTHORITY_CHANGE_DEFAULT_ROLE);

            // then
            assertAll(
                    () -> assertThat(officerRole.isDefault()).isFalse(),
                    () -> assertThat(defaultRoles.get(OFFICER).isDefault()).isTrue(),
                    () -> assertThat(generalRole.isDefault()).isFalse(),
                    () -> assertThat(defaultRoles.get(GENERAL).isDefault()).isTrue()
            );
        }

        @Test
        @DisplayName("changeDefaultRole() 시 첫번째 Role이 이미 기본 역할인 경우 예외가 발생한다.")
        void fail_test_14() {
            // given
            Member member = member(null);
            Club club = club(null);
            Map<ClubRoleCategory, ClubRole> defaultRoles = ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);

            Participant manager = participant(null, member, club, defaultRoles.get(PRESIDENT));

            // when
            assertThat(
                    assertThrows(ClubRoleException.class, () ->
                            manager.changeDefaultRole(defaultRoles.get(OFFICER), officerRole)
                    ).exceptionType()
            ).isEqualTo(ALREADY_DEFAULT_ROLE);

            assertThat(
                    assertThrows(ClubRoleException.class, () ->
                            manager.changeDefaultRole(defaultRoles.get(GENERAL), defaultRoles.get(GENERAL))
                    ).exceptionType()
            ).isEqualTo(ALREADY_DEFAULT_ROLE);

            // then
            assertAll(
                    () -> assertThat(officerRole.isDefault()).isFalse(),
                    () -> assertThat(defaultRoles.get(OFFICER).isDefault()).isTrue(),
                    () -> assertThat(generalRole.isDefault()).isFalse(),
                    () -> assertThat(defaultRoles.get(GENERAL).isDefault()).isTrue()
            );
        }

        @Test
        @DisplayName("changeDefaultRole() 시 두 인자로 들어오는 역할의 카테고리가 일치하지 않으면 예외가 발생한다.")
        void fail_test_16() {
            // given
            Member member = member(null);
            Club club = club(null);
            Map<ClubRoleCategory, ClubRole> defaultRoles = ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));

            ClubRole officerRole = officerRole("임원", club);
            ClubRole generalRole = generalRole("일반", club);

            Participant manager = participant(null, member, club, defaultRoles.get(PRESIDENT));

            // when
            assertThat(
                    assertThrows(ClubRoleException.class, () ->
                            manager.changeDefaultRole(officerRole, defaultRoles.get(GENERAL))
                    ).exceptionType()
            ).isEqualTo(MISMATCH_EXISTING_DEFAULT_ROLE_AND_CANDIDATE);

            assertThat(
                    assertThrows(ClubRoleException.class, () ->
                            manager.changeDefaultRole(generalRole, defaultRoles.get(OFFICER))
                    ).exceptionType()
            ).isEqualTo(MISMATCH_EXISTING_DEFAULT_ROLE_AND_CANDIDATE);

            // then
            assertAll(
                    () -> assertThat(officerRole.isDefault()).isFalse(),
                    () -> assertThat(defaultRoles.get(OFFICER).isDefault()).isTrue(),
                    () -> assertThat(generalRole.isDefault()).isFalse(),
                    () -> assertThat(defaultRoles.get(GENERAL).isDefault()).isTrue()
            );
        }
    }
}