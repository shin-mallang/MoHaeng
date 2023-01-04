package com.mohaeng.infrastructure.persistence.database.repository.event;

import com.mohaeng.domain.config.event.BaseEventHistory;
import com.mohaeng.domain.config.event.EventHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEventHistoryRepository extends JpaRepository<BaseEventHistory, Long>, EventHistoryRepository {
}
