package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.ProgrammeDto;
import com.znz.tpip_backend.dto.ProgrammeRecommendationDto;
import com.znz.tpip_backend.model.Programme;
import com.znz.tpip_backend.service.ProgrammeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programmes")
@RequiredArgsConstructor
public class ProgrammeController {

    private final ProgrammeService programmeService;

    // ================= ADMIN =================

    // CREATE PROGRAMME (ADMIN)
    @PostMapping
    public ProgrammeDto create(@RequestBody ProgrammeDto dto) {
        return programmeService.createProgramme(dto);
    }

    // UPDATE PROGRAMME (ADMIN)
    @PutMapping("/{id}")
    public ProgrammeDto update(@PathVariable Long id, @RequestBody ProgrammeDto dto) {
        return programmeService.updateProgramme(id, dto);
    }

    // DELETE PROGRAMME (ADMIN)
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        programmeService.deleteProgramme(id);
        return "Programme deleted";
    }

    // ================= APPLICANT =================

    // BROWSE
    @GetMapping
    public List<Programme> getAll() {
        return programmeService.getAllProgrammes();
    }

    // FILTER
    @GetMapping("/filter")
    public List<Programme> filter(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String location
    ) {
        return programmeService.filterProgrammes(department, field, location);
    }

    // RECOMMENDATION
    @GetMapping("/recommendations/{applicantId}")
    public List<ProgrammeRecommendationDto> recommend(@PathVariable Long applicantId) {
        return programmeService.recommendProgrammes(applicantId);
    }
}