package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.RefereeDTO;
import com.znz.tpip_backend.dto.RefereeTokenValidationDTO;
import com.znz.tpip_backend.service.RefereeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/referees")
@RequiredArgsConstructor
public class RefereeController {

    private final RefereeService refereeService;

    @PostMapping
    public ResponseEntity<RefereeDTO> create(@RequestBody RefereeDTO dto) {
        return ResponseEntity.ok(refereeService.create(dto));
    }

    @GetMapping("/application/{id}")
    public ResponseEntity<List<RefereeDTO>> getByApplication(@PathVariable Long id) {
        return ResponseEntity.ok(refereeService.getByApplication(id));
    }

     @GetMapping("/validate-token")
    public ResponseEntity<RefereeTokenValidationDTO> validateToken(
            @RequestParam String token) {

        RefereeTokenValidationDTO response =
                refereeService.validateToken(token);

        return ResponseEntity.ok(response);
    }
}