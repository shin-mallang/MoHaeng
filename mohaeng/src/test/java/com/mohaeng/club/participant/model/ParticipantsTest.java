package com.mohaeng.club.participant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Participants 은")
class ParticipantsTest {

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
}