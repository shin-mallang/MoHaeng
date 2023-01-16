package com.mohaeng.alarm.application.service;

import com.mohaeng.alarm.application.usecase.QueryAlarmByIdUseCase;
import com.mohaeng.alarm.application.usecase.dto.AlarmDto;
import com.mohaeng.alarm.domain.model.Alarm;
import com.mohaeng.alarm.domain.repository.AlarmRepository;
import com.mohaeng.alarm.exception.AlarmException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.common.fixtures.AlarmFixture;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.alarm.exception.AlarmExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationTest
@DisplayName("QueryAlarmById 는 ")
class QueryAlarmByIdTest {

    @Autowired
    private QueryAlarmByIdUseCase queryAlarmByIdUseCase;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("id가 일치하는 알람이 존재한다면, 조회한다.")
    void success_test_1() {
        // given
        Member member = memberRepository.save(member(null));
        Alarm save = alarmRepository.save(AlarmFixture.alarmWithMember(member));

        // when
        AlarmDto query = queryAlarmByIdUseCase.query(new QueryAlarmByIdUseCase.Query(save.id(), member.id()));

        // then
        assertAll(
                () -> assertThat(query.createdAt()).isEqualTo(save.createdAt()),
                () -> assertThat(query.title()).isEqualTo(save.alarmMessage().title()),
                () -> assertThat(query.content()).isEqualTo(save.alarmMessage().content()),
                () -> assertThat(query.alarmType()).isEqualTo(save.alarmType().name())
        );
    }

    @Test
    @DisplayName("알람 조회 시 읽음 처리를 수행한다.")
    void success_test_2() {
        // given
        Member member = memberRepository.save(member(null));
        Alarm save = alarmRepository.save(AlarmFixture.alarmWithMember(member));

        // when
        AlarmDto query = queryAlarmByIdUseCase.query(new QueryAlarmByIdUseCase.Query(save.id(), member.id()));

        // then
        assertThat(query.isRead()).isTrue();
    }

    @Test
    @DisplayName("id가 일치하는 알람이 존재하지 않는다면 오류를 발생시킨다.")
    void fail_test_1() {
        // when & then
        BaseExceptionType baseExceptionType = assertThrows(AlarmException.class,
                () -> queryAlarmByIdUseCase.query(new QueryAlarmByIdUseCase.Query(1L, 1L)))
                .exceptionType();

        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_APPLICATION_FORM);
    }
}