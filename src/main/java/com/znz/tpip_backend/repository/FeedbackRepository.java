package com.znz.tpip_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.dto.FeedbackDto;
import com.znz.tpip_backend.model.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    boolean existsByMentorIdAndInternIdAndDate(Long mentorId, Long internId, LocalDate date);

    // Order is random ❌ - we want newest first
    // List<Feedback> findByInternId(Long internId);

    // List<Feedback> findByMentorId(Long mentorId);

    // List<Feedback> findByPlacementId(Long placementId);

    // This gives: newest logs first (real-world expectation)
    List<Feedback> findByInternIdOrderByDateDesc(Long internId);

    List<Feedback> findByMentorIdOrderByDateDesc(Long mentorId);

    List<Feedback> findByPlacementIdOrderByDateDesc(Long placementId);

    List<Feedback> findByPlacementId(Long id);
}
