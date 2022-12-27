package com.mohaeng.domain.member.domain;

import com.mohaeng.application.member.dto.CreateMemberDto;

public interface MemberCommand {

    void save(final CreateMemberDto createMemberDto);
}
