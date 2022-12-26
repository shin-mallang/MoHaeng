package com.mohaeng.infrastructure.persistence.database.service.member;

import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.member.MemberRepository;
import com.mohaeng.infrastructure.persistence.database.service.member.dto.CreateMemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("MemberCommand는 ")
class MemberCommandTest {

    private final MemberRepository mockMemberRepository = mock(MemberRepository.class);
    private final CreateMemberDto createMemberDto = mock(CreateMemberDto.class);

    @Test
    @DisplayName("save() 수행 시 repository의 save()를 호출한다. ")
    void save() {
        // given
        MemberCommand memberCommand = new MemberCommand(mockMemberRepository);

        // when
        memberCommand.save(createMemberDto);

        // then
        verify(mockMemberRepository, times(1))
                .save(any(MemberJpaEntity.class));
    }
}