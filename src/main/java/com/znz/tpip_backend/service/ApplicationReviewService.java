package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.ApplicationReviewDTO;
import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.enums.ReviewDecision;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.ApplicationReview;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.ApplicationReviewRepository;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationEventPublisherService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationReviewService {

    private final ApplicationReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisherService eventPublisher;

    // ================= CREATE REVIEW =================
    public ApplicationReviewDTO review(ApplicationReviewDTO dto) {

        Application app = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        ApplicationReview review = modelMapper.map(dto, ApplicationReview.class);
        review.setApplication(app);
        review.setReviewedAt(LocalDateTime.now());

        ApplicationReview saved = reviewRepository.save(review);

        // ================= STATUS UPDATE =================
        updateApplicationStatus(app, review.getDecision());

        // ================= EVENT TRIGGER =================
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep()
        );

        return modelMapper.map(saved, ApplicationReviewDTO.class);
    }

    // ================= BUSINESS RULE =================
    private void updateApplicationStatus(Application app, ReviewDecision decision) {

        switch (decision) {

            case UNDER_REVIEW -> app.setStatus(ApplicationStatus.UNDER_REVIEW);

            case SHORTLISTED_FOR_INTERVIEW -> {
                app.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
            }

            case ACCEPTED -> app.setStatus(ApplicationStatus.ACCEPTED);

            case REJECTED -> app.setStatus(ApplicationStatus.REJECTED);

            default -> {}
        }
    }

    // ================= GET REVIEWS =================
    public List<ApplicationReviewDTO> getByApplication(Long applicationId) {

        return reviewRepository.findByApplicationId(applicationId)
                .stream()
                .map(r -> modelMapper.map(r, ApplicationReviewDTO.class))
                .toList();
    }
}