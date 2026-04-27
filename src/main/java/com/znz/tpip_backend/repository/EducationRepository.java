package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.Education;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByApplicantId(Long applicantId);
}