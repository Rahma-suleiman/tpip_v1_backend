package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.PersonalInfoDto;
import com.znz.tpip_backend.service.PersonalInfoService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tpip/personal-info")
@RequiredArgsConstructor
public class PersonalInfoController {

    private final PersonalInfoService personalInfoService;

    @PostMapping("/{applicantId}")
    public ResponseEntity<PersonalInfoDto> save(@PathVariable Long applicantId, @RequestBody PersonalInfoDto dto) {
        return ResponseEntity.ok( personalInfoService.save(applicantId, dto));
    }

    @GetMapping("/{applicantId}")
    public PersonalInfoDto get(@PathVariable Long applicantId) {
        return personalInfoService.getByApplicant(applicantId);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        personalInfoService.delete(id);
        return "Deleted successfully";
    }
}