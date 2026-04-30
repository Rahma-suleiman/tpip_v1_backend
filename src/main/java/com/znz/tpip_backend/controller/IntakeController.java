package com.znz.tpip_backend.controller;

import org.springframework.web.bind.annotation.*;

import com.znz.tpip_backend.dto.IntakeDTO;
import com.znz.tpip_backend.model.Intake;
import com.znz.tpip_backend.service.IntakeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/intakes")
@RequiredArgsConstructor
public class IntakeController {

    private final IntakeService intakeService;

    @PostMapping
    public IntakeDTO create(@RequestBody IntakeDTO dto) {
        return intakeService.create(dto);
    }

    @PutMapping("/activate/{id}")
    public IntakeDTO activate(@PathVariable Long id) {
        return intakeService.activateIntake(id);
    }

    @GetMapping("/active")
    public IntakeDTO getActive() {
        return intakeService.getActiveIntake();
    }
}
