package com.mohaeng.common.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEventHistoryRepository extends JpaRepository<BaseEventHistory, Long>, EventHistoryRepository {
}
