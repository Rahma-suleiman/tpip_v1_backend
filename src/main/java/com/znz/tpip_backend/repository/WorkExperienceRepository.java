package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {

    List<WorkExperience> findByApplicantId(Long applicantId);

    void deleteByApplicantId(Long applicantId);
}