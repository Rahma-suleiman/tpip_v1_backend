package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.ApplicationReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationReviewRepository extends JpaRepository<ApplicationReview, Long> {

    List<ApplicationReview> findByApplicationId(Long applicationId);
}