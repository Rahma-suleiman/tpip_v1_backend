package com.znz.tpip_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.znz.tpip_backend.dto.RefereeSubmissionDTO;
import com.znz.tpip_backend.service.RefereeSubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/referee-submissions")
@RequiredArgsConstructor
public class RefereeSubmissionController {

    private final RefereeSubmissionService service;

    @PostMapping("/{refereeId}")
    public ResponseEntity<RefereeSubmissionDTO> submit(
            @PathVariable Long refereeId,
            @RequestBody RefereeSubmissionDTO dto) {

        return ResponseEntity.ok(service.submit(refereeId, dto));
    }
}