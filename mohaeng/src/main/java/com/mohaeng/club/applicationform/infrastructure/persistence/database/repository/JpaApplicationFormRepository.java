package com.mohaeng.club.applicationform.infrastructure.persistence.database.repository;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaApplicationFormRepository extends JpaRepository<ApplicationForm, Long>, ApplicationFormRepository {
}
