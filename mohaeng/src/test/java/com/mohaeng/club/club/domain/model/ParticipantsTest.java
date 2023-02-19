package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_PRESIDENT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Participants 은")
class ParticipantsTest {

    private final Club club = club(1L);
    private final Map<ClubRoleCategory, ClubRole> clubRoleMap =
            ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));

    private Participant president;
    private Participant officer;
    private Participant general;
    private Participants participants;

    @BeforeEach
    void init() {
        president = new Participant(member(1L), club, clubRoleMap.get(PRESIDENT));
        officer = new Participant(member(2L), club, clubRoleMap.get(OFFICER));
        general = new Participant(member(3L), club, clubRoleMap.get(GENERAL));
        participants = Participants.initWithPresident(president);
        participants.register(officer);
        participants.register(general);
    }

    @Test
    void initWithPresident_는_회장만을_포함한_Participants_를_반환한다() {
        // when
        Participants participants = Participants.initWithPresident(president);

        // then
        assertThat(participants.participants().size()).isEqualTo(1);
        assertThat(participants.participants().get(0)).isEqualTo(president);
    }

    @Test
    void initWithPresident_는_회장이_아닌_경우_예외를_발생한다() {
        // when
        BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                Participants.initWithPresident(officer)
        ).exceptionType();
        BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                Participants.initWithPresident(general)
        ).exceptionType();

        // then
        assertThat(baseExceptionType1).isEqualTo(NOT_PRESIDENT);
        assertThat(baseExceptionType2).isEqualTo(NOT_PRESIDENT);
    }

    @Test
    void findByMemberId_는_MemberId가_일치하는_참여자를_반환한다() {
        // when & then
        assertThat(participants.findByMemberId(president.member().id())).isPresent();
        assertThat(participants.findByMemberId(officer.member().id())).isPresent();
        assertThat(participants.findByMemberId(general.member().id())).isPresent();
        assertThat(participants.findByMemberId(1000L)).isEmpty();
    }

    @Test
    void findAllManager_는_모든_임원_혹은_회장을_반환한다() {
        // when
        List<Participant> allManager = participants.findAllManager();

        // then
        assertThat(allManager)
                .containsExactlyInAnyOrder(president, officer);
    }

    @Test
    void findPresident_는_회장을_반환한다() {
        // when
        Participant president = participants.findPresident();

        // then
        assertThat(president.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT);
    }

    @Test
    void register_시_회원을_등록한다() {
        // given
        Participant participant = new Participant(member(3L), club, clubRoleMap.get(GENERAL));

        // when
        participants.register(participant);

        // then
        assertThat(participants.participants()).contains(participant);
    }

    @Test
    void delete_시_참여자를_제거한다() {
        // when
        participants.delete(general);

        // then
        assertThat(participants.participants()).doesNotContain(general);
    }

    @Test
    void delete_시_회장은_모임을_탈퇴할_수_없다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                participants.delete(president)
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(PRESIDENT_CAN_NOT_LEAVE_CLUB);
    }
}