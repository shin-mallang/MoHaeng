package com.mohaeng.alarm.presentation;

import com.mohaeng.alarm.application.usecase.QueryAlarmByIdUseCase;
import com.mohaeng.alarm.application.usecase.dto.AlarmDto;
import com.mohaeng.alarm.presentation.mapper.AlarmControllerMapper;
import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class QueryAlarmByIdController {

    public static final String QUERY_ALARM_BY_ID = "/api/alarm/{id}";

    private final QueryAlarmByIdUseCase queryAlarmByIdUseCase;

    public QueryAlarmByIdController(final QueryAlarmByIdUseCase queryAlarmByIdUseCase) {
        this.queryAlarmByIdUseCase = queryAlarmByIdUseCase;
    }

    @GetMapping(path = QUERY_ALARM_BY_ID)
    public ResponseEntity<AlarmResponse> queryById(
            @Auth Long memberId,
            @PathVariable("id") Long id
    ) {
        AlarmDto alarmDto = queryAlarmByIdUseCase.query(new QueryAlarmByIdUseCase.Query(id, memberId));

        return ResponseEntity.ok(AlarmControllerMapper.toResponseDto(alarmDto));
    }

    public record AlarmResponse(
            LocalDateTime createdAt,  // 알람 발송일
            String title,
            String content,
            String alarmType,
            boolean isRead  // 알람 읽음 여부
    ) {
    }
}
