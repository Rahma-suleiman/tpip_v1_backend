package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.ApplicationReviewDTO;
import com.znz.tpip_backend.service.ApplicationReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ApplicationReviewController {

    private final ApplicationReviewService service;

    @PostMapping
    public ResponseEntity<ApplicationReviewDTO> review(
            @RequestBody ApplicationReviewDTO dto) {

        return ResponseEntity.ok(service.review(dto));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<List<ApplicationReviewDTO>> getReviews(
            @PathVariable Long applicationId) {

        return ResponseEntity.ok(service.getByApplication(applicationId));
    }
}