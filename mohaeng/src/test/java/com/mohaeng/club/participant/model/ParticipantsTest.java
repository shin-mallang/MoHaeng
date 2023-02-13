package com.mohaeng.club.participant.model;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.participant.domain.model.Participant;
import com.mohaeng.club.participant.domain.model.Participants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_CLUB;
import static com.mohaeng.common.fixtures.MemberFixture.MALLANG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Participants 은")
class ParticipantsTest {

    private final Club club = ANA_CLUB;
    private final Map<ClubRoleCategory, ClubRole> clubRoleMap =
            ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));

    @Test
    void initWithPresident_는_회장만을_포함한_Participants_를_반환한다() {
        // given
        Participant participant = mock(Participant.class);

        // when
        Participants participants = Participants.initWithPresident(participant);

        // then
        assertThat(participants.participants().size()).isEqualTo(1);
        assertThat(participants.participants().get(0)).isEqualTo(participant);
    }

    @Test
    void isManager_는_회장_혹은_임원인_경우_true를_반환한다() {
        // given
        Participant president = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(PRESIDENT));
        Participant officer = new Participant(MALLANG, ANA_CLUB, clubRoleMap.get(OFFICER));

        // when

        // then
    }

    @Test
    void isPresident_는_회장인_경우_true를_반환한다() {
        // given

        // when

        // then
    }
}