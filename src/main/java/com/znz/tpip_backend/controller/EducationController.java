package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.EducationDto;
import com.znz.tpip_backend.service.EducationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tpip/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<EducationDto> create(@RequestBody EducationDto dto) {
        EducationDto response = educationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationDto> update(@PathVariable Long id, @RequestBody EducationDto dto) {
        EducationDto response = educationService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<EducationDto>> getByApplicant(@PathVariable Long applicantId) {
        List<EducationDto> response = educationService.getByApplicant(applicantId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        educationService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}