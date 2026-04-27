package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.PersonalInfo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.Optional;

public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, Long> {

    Optional<PersonalInfo> findByApplicantId(Long applicantId);

    // Optional<PersonalInfo> findByApplicantId(Long applicantId);

    // boolean existsByApplicantId(Long applicantId);
    
}