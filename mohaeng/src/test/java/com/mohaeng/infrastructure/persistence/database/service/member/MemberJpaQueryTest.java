package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import com.mohaeng.infrastructure.persistence.database.service.member.exception.NotFoundMemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("MemberJpaQuery는 ")
class MemberJpaQueryTest {

    private final MemberRepository mockMemberRepository = mock(MemberRepository.class);
    private final MemberJpaQuery memberJpaQuery = new MemberJpaQuery(mockMemberRepository);

    @Test
    @DisplayName("existsByUsername() 수행 시 repository의 existsByUsername()을 호출한다.")
    void existsByUsername() {
        // when
        memberJpaQuery.existsByUsername("username");

        // then
        verify(mockMemberRepository, times(1))
                .existsByUsername(any(String.class));
    }

    @Test
    @DisplayName("findByUsername() 수행 시 존재한다면 MemberJpaEntity를 반환한다.")
    void findByUsername() {
        // given
        when(mockMemberRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.of(mock(MemberJpaEntity.class)));

        // when
        Member member = memberJpaQuery.findByUsername("username");

        // then
        assertAll(
                () -> assertThat(member).isNotNull(),
                () -> verify(mockMemberRepository, times(1)).findByUsername(any(String.class))
        );
    }

    @Test
    @DisplayName("findByUsername() 수행 시 존재하지 않는다면 예외를 던진다.")
    void findByUsernameFailWillThrowException() {
        // given
        when(mockMemberRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberJpaQuery.findByUsername("username"))
                .isInstanceOf(NotFoundMemberException.class);
    }
}