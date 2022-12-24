package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("MemberQuery는 ")
class MemberQueryTest {

    private final MemberRepository mockMemberRepository = mock(MemberRepository.class);

    @Test
    @DisplayName("existsByUsername() 수행 시 repository의 existsByUsername()을 호출한다.")
    void existsByUsername() {
        // given
        MemberQuery memberQuery = new MemberQuery(mockMemberRepository);

        // when
        memberQuery.existsByUsername("username");

        // then
        verify(mockMemberRepository, times(1))
                .existsByUsername(any(String.class));
    }
}