package com.mohaeng.alarm.application.service.generator;

import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.applicationform.domain.event.RequestJoinClubEvent;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.common.fixtures.ApplicationForeFixture;
import com.mohaeng.common.repositories.MockApplicationFormRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mohaeng.common.fixtures.ApplicationForeFixture.applicationForm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("RequestClubJoinAlarmContentGenerator 는 ")
class RequestClubJoinAlarmContentGeneratorTest {

    private static final String TITLE = "모임 가입 신청이 왔습니다.";
    private static final String NAME_WITH_ID_FORMAT = "%s({%s})";  // ex: 신동훈({1})
    private static final String MESSAGE_FORMAT = "%s님이 %s모임에 가입 신청을 보냈습니다. (ApplicationFormId:{%d})";

    private final ApplicationFormRepository applicationFormRepository = new MockApplicationFormRepository();
    private final RequestClubJoinAlarmContentGenerator clubJoinAlarmContentGenerator = new RequestClubJoinAlarmContentGenerator(applicationFormRepository);

    /**
     * %s님이 %s모임에 가입 신청을 보냈습니다. (ApplicationFormId:{%d})
     */
    @Test
    @DisplayName("RequestJoinClubEvent 를 받아 메세지를 생성한다.")
    void test() {
        // given
        final Long memberId = 10L;
        final Long clubId = 123L;
        ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(memberId, clubId, null));

        RequestJoinClubEvent requestJoinClubEvent = ApplicationForeFixture.requestJoinClubEvent(List.of(1L));

        // when
        AlarmMessage alarmMessage = clubJoinAlarmContentGenerator.generate(requestJoinClubEvent);

        // then
        assertAll(
                () -> assertThat(alarmMessage.title()).isEqualTo(TITLE),
                () -> assertThat(alarmMessage.content()).isEqualTo(MESSAGE_FORMAT.formatted(
                        NAME_WITH_ID_FORMAT.formatted(applicationForm.applicant().name(), applicationForm.applicant().id()),
                        NAME_WITH_ID_FORMAT.formatted(applicationForm.target().name(), applicationForm.target().id()),
                        applicationForm.id()
                ))
        );
    }
}