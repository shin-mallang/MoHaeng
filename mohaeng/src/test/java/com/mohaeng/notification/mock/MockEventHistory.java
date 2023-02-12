package com.mohaeng.notification.mock;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MOCK")
public class MockEventHistory extends BaseEventHistory {
}
