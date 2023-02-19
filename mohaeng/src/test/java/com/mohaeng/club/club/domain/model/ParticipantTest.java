package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Participant 은")
class ParticipantTest {

    private final Club club = club(1L);
    private final Map<ClubRoleCategory, ClubRole> clubRoleMap =
            ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));

    private Participant president;
    private final Participant officer = new Participant(member(2L), club, clubRoleMap.get(OFFICER));
    private final Participant general = new Participant(member(3L), club, clubRoleMap.get(GENERAL));

    @BeforeEach
    void init() {
        president = club.findPresident();
        club.participants().participants().add(officer);
        club.participants().participants().add(general);
    }

    @Test
    void isManager_는_회장_혹은_임원인_경우_true를_반환한다() {
        // when & then
        assertThat(president.isManager()).isTrue();
        assertThat(officer.isManager()).isTrue();
        assertThat(general.isManager()).isFalse();
    }

    @Test
    void isPresident_는_회장인_경우_true를_반환한다() {
        // when & then
        assertThat(president.isPresident()).isTrue();
        assertThat(officer.isPresident()).isFalse();
        assertThat(general.isPresident()).isFalse();
    }

    @Test
    void expel_시_대상_참여자를_모임에서_추방한다() {
        // given
        int before = club.currentParticipantCount();

        // when
        president.expel(officer);
        president.expel(general);

        // then
        assertThat(club.currentParticipantCount()).isEqualTo(before - 2);
        assertThat(club.findParticipantByMemberId(officer.member().id())).isEmpty();
        assertThat(club.findParticipantByMemberId(general.member().id())).isEmpty();
    }

    @Test
    void expel_시_회장이_아닌_경우_추방할_수_없다() {
        // given
        int before = club.currentParticipantCount();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                officer.expel(general)
        ).exceptionType();

        // then
        assertThat(club.currentParticipantCount()).isEqualTo(before);
        assertThat(club.findParticipantByMemberId(general.member().id())).isPresent();
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT);
    }

    @Test
    void expel_시_대상_참여자와_같은_모임이_아닌_경우_추방할_수_없다() {
        // given
        Long presidentId = 11L;
        Long generalId = 12L;
        Club other = clubWithMember(member(presidentId));
        other.registerParticipant(member(generalId));
        Participant otherPresident = other.findParticipantByMemberId(presidentId).get();
        Participant otherGeneral = other.findParticipantByMemberId(generalId).get();

        // when
        BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                president.expel(otherPresident)
        ).exceptionType();

        BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                president.expel(otherGeneral)
        ).exceptionType();
        BaseExceptionType baseExceptionType3 = assertThrows(ParticipantException.class, () ->
                otherPresident.expel(president)
        ).exceptionType();

        BaseExceptionType baseExceptionType4 = assertThrows(ParticipantException.class, () ->
                otherPresident.expel(general)
        ).exceptionType();

        // then
        assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT);
        assertThat(baseExceptionType2).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT);
        assertThat(baseExceptionType3).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT);
        assertThat(baseExceptionType4).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT);
    }
}