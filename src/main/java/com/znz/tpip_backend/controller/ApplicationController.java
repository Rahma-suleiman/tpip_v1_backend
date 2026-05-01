package com.znz.tpip_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.znz.tpip_backend.dto.ApplicationProgressDto;
import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.enums.ApplicationStep;
// import com.znz.tpip_backend.dto.ApplicationDto;
import com.znz.tpip_backend.model.Applicant;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.User;
import com.znz.tpip_backend.repository.UserRepository;
import com.znz.tpip_backend.service.ApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tpip/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    // START APPLICATION
    @GetMapping("/start")
    public ResponseEntity<Application> startApplication(@RequestParam Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Applicant applicant = user.getApplicant();

        Application app = applicationService.createOrGetApplication(applicant.getId());

        return ResponseEntity.ok(app);
    }

    // MOVE STEP
    @PutMapping("/{id}/step")
    public ResponseEntity<Application> moveStep(
            @PathVariable Long id,
            @RequestParam ApplicationStep step) {

        return ResponseEntity.ok(applicationService.moveToStep(id, step));
    }

    // SUBMIT APPLICATION
    @PostMapping("/{id}/submit")
    public ResponseEntity<Application> submit(@PathVariable Long id) {

        return ResponseEntity.ok(applicationService.submitApplication(id));
    }

    // UPDATE STATUS (ADMIN)
    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {

        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }

    // ================= CHECK CURRENT STEP =================
    @GetMapping("/{applicationId}/current-step")
    public ApplicationProgressDto getCurrentStep(@PathVariable Long applicationId) {
        return applicationService.getCurrentStep(applicationId);
    }

}
// @RestController
// @RequestMapping("/api/v1/tpip/application")
// @RequiredArgsConstructor
// public class ApplicationController {

// private final ApplicationService applicationService;
// private final UserRepository userRepository;

// // ================= START APPLICATION =================
// @GetMapping("/start")
// public ResponseEntity<Application> startApplication(@RequestParam Long
// userId) {

// User user = userRepository.findById(userId)
// .orElseThrow(() -> new RuntimeException("User not found"));

// Applicant applicant = user.getApplicant();

// Application app =
// applicationService.createOrGetApplication(applicant.getId());

// return ResponseEntity.ok(app);
// }

// // ================= MOVE STEP =================
// @PutMapping("/{id}/step")
// public ResponseEntity<Application> moveStep(
// @PathVariable Long id,
// @RequestParam ApplicationStep step) {

// return ResponseEntity.ok(applicationService.moveToStep(id, step));
// }

// // ================= SUBMIT =================
// @PutMapping("/{id}/submit")
// public ResponseEntity<Application> submit(@PathVariable Long id) {
// return ResponseEntity.ok(applicationService.submitApplication(id));
// }

// // ================= ADMIN ACTIONS =================

// @PutMapping("/{id}/review")
// public ResponseEntity<Application> review(@PathVariable Long id) {
// return ResponseEntity.ok(applicationService.markUnderReview(id));
// }

// @PutMapping("/{id}/interview")
// public ResponseEntity<Application> interview(@PathVariable Long id) {
// return ResponseEntity.ok(applicationService.markForInterview(id));
// }

// @PutMapping("/{id}/accept")
// public ResponseEntity<Application> accept(@PathVariable Long id) {
// return ResponseEntity.ok(applicationService.acceptApplication(id));
// }

// @PutMapping("/{id}/reject")
// public ResponseEntity<Application> reject(@PathVariable Long id) {
// return ResponseEntity.ok(applicationService.rejectApplication(id));
// }

// @PutMapping("/{id}/waitlist")
// public ResponseEntity<Application> waitlist(@PathVariable Long id) {
// return ResponseEntity.ok(applicationService.waitlistApplication(id));
// }
// }
