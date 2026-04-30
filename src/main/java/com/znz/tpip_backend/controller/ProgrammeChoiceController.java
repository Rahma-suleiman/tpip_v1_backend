package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.ProgrammeChoiceDto;
import com.znz.tpip_backend.service.ProgrammeChoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programme-choices")
@RequiredArgsConstructor
public class ProgrammeChoiceController {

    private final ProgrammeChoiceService programmeChoiceService;

    // ================= CREATE =================
    @PostMapping
    public ProgrammeChoiceDto create(@RequestBody ProgrammeChoiceDto dto) {
        return programmeChoiceService.create(dto);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ProgrammeChoiceDto update(@PathVariable Long id, @RequestBody ProgrammeChoiceDto dto) {
        return programmeChoiceService.update(id, dto);
    }

    // ================= GET BY APPLICATION =================
    @GetMapping("/application/{applicationId}")
    public List<ProgrammeChoiceDto> getByApplication(@PathVariable Long applicationId) {
        return programmeChoiceService.getByApplication(applicationId);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        programmeChoiceService.delete(id);
        return "Deleted successfully";
    }
}