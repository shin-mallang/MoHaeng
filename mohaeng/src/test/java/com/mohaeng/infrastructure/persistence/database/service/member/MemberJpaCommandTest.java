package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.domain.member.domain.Member;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("MemberJpaCommand는 ")
class MemberJpaCommandTest {

    private final MemberRepository mockMemberRepository = mock(MemberRepository.class);
    private final Member member = mock(Member.class);

    @Test
    @DisplayName("save() 수행 시 repository의 save()를 호출한다. ")
    void save() {
        // given
        MemberJpaCommand memberJpaCommand = new MemberJpaCommand(mockMemberRepository);

        // when
        memberJpaCommand.save(member);

        // then
        verify(mockMemberRepository, times(1))
                .save(any(MemberJpaEntity.class));
    }
}