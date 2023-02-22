package com.mohaeng.club.applicationform.infrastructure.persistence.database.repository;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaApplicationFormRepository extends JpaRepository<ApplicationForm, Long>, ApplicationFormRepository {

    @Override
    @Modifying
    @Query("delete from ApplicationForm af where af.club.id = :clubId")
    void deleteAllByClubId(@Param("clubId") final Long clubId);

    @Override
    @Query("select af from ApplicationForm af join fetch af.applicant where af.club.id = :clubId and af.processed = false")
    List<ApplicationForm> findAllWithApplicantByClubIdAndProcessedFalse(@Param("clubId") final Long clubId);
}
