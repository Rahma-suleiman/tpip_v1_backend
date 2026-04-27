package com.znz.tpip_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.model.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    boolean existsByIndexNumber(String indexNumber);

    // boolean existsByApplicationIndexNumber(String index);
}
