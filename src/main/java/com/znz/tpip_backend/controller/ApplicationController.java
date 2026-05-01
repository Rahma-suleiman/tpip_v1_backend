package com.znz.tpip_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.znz.tpip_backend.dto.ApplicationDTO;
import com.znz.tpip_backend.dto.ApplicationProgressDto;
import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.Applicant;
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

    // ================= START APPLICATION =================
    @GetMapping("/start")
    public ResponseEntity<ApplicationDTO> startApplication(@RequestParam Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Applicant applicant = user.getApplicant();

        return ResponseEntity.ok(
                applicationService.createOrGetApplication(applicant.getId())
        );
    }

    // ================= GET FULL APPLICATION =================
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplication(@PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getApplicationDTO(id)
        );
    }

    // ================= MOVE STEP =================
    @PutMapping("/{id}/step")
    public ResponseEntity<ApplicationDTO> moveStep(
            @PathVariable Long id,
            @RequestParam ApplicationStep step) {

        return ResponseEntity.ok(
                applicationService.moveToStep(id, step)
        );
    }

    // ================= SUBMIT APPLICATION =================
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApplicationDTO> submit(@PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.submitApplication(id)
        );
    }

    // ================= UPDATE STATUS (ADMIN) =================
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {

        return ResponseEntity.ok(
                applicationService.updateStatus(id, status)
        );
    }

    // ================= CURRENT STEP / PROGRESS =================
    @GetMapping("/{id}/current-step")
    public ResponseEntity<ApplicationProgressDto> getCurrentStep(@PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getCurrentStep(id)
        );
    }
}