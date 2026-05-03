package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.WorkExperienceDto;
import com.znz.tpip_backend.service.WorkExperienceService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-experience")
@RequiredArgsConstructor
public class WorkExperienceController {

    private final WorkExperienceService service;

    // CREATE
    @PostMapping
    public ResponseEntity<WorkExperienceDto> create(@RequestBody WorkExperienceDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // // UPDATE
    // @PutMapping("/{id}")
    // public ResponseEntity<WorkExperienceDto> update(
    //         @PathVariable Long id,
    //         @RequestBody WorkExperienceDto dto) {
    //     return ResponseEntity.ok(service.update(id, dto));
    // }
     // PATCH UPDATE (better semantic than PUT for partial updates)
    @PatchMapping("/{id}")
    public ResponseEntity<WorkExperienceDto> update(
            @PathVariable Long id,
            @RequestBody WorkExperienceDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // GET BY APPLICANT
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<WorkExperienceDto>> getByApplicant(@PathVariable Long applicantId) {
        return ResponseEntity.ok(service.getByApplicant(applicantId));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}