package com.znz.tpip_backend.service;

// import org.aspectj.lang.annotation.Before;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.znz.tpip_backend.dto.IntakeDTO;
import com.znz.tpip_backend.model.Intake;
import com.znz.tpip_backend.repository.IntakeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IntakeService {

    private final IntakeRepository intakeRepository;
    private final ModelMapper modelMapper;

    public IntakeDTO create(IntakeDTO dto) {
        Intake intake = modelMapper.map(dto, Intake.class);
        return modelMapper.map(intakeRepository.save(intake), IntakeDTO.class);
    }

    // Ensure ONLY one intake is active at a time.
    // Before activating a new intake with this ID:→ deactivate all others
    public IntakeDTO activateIntake(Long id) {

        // deactivate all
        intakeRepository.findAll().forEach(i -> {
            i.setActive(false);
            intakeRepository.save(i);
        });

        // activate selected intake id (find selected intake id)
        Intake intake = intakeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intake not found"));

        intake.setActive(true);

        return modelMapper.map(intakeRepository.save(intake), IntakeDTO.class);
    }

    // Fetch the currently active intake.
    public IntakeDTO getActiveIntake() {
        Intake intake = intakeRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active intake found"));

        return modelMapper.map(intake, IntakeDTO.class);
    }
}
// now since all post are not active go to this endpoint 
// 'PUT' /api/intakes/activate/{id} to activate the current year intake
// {
//   "name": "2026/2027",
//   "startDate": "2026-10-01T08:00:00Z",
//   "endDate": "2027-06-30T17:00:00Z",
//   "active": false
// }
// {
//   "name": "2025/2026",
//   "startDate": "2025-10-01T08:00:00Z",
//   "endDate": "2026-06-30T17:00:00Z",
//   "active": false
// }
// {
//   "name": "2024/2025",
//   "startDate": "2024-10-01T08:00:00Z",
//   "endDate": "2025-06-30T17:00:00Z",
//   "active": false
// }