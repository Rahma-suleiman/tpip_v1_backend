package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.RefereeDTO;
import com.znz.tpip_backend.dto.RefereeTokenValidationDTO;
import com.znz.tpip_backend.email.RefereeEmailService;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.RefereeStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Referee;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.RefereeRepository;
import com.znz.tpip_backend.service.common.PatchEngine;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationEventPublisherService;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationStepGuard;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefereeService {

    private final ApplicationStepGuard stepGuard;
    private final RefereeRepository refereeRepository;
    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisherService eventPublisher;
    private final RefereeEmailService refereeEmailService;
    private final TokenService tokenService;
    private final PatchEngine patchEngine;

    // ================= CREATE REFEREE =================
    public RefereeDTO create(RefereeDTO dto) {

        Application app = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        stepGuard.validateStep(app, ApplicationStep.REFEREES);

        Referee referee = modelMapper.map(dto, Referee.class);

        String token = tokenService.generateToken();

        referee.setApplication(app);
        referee.setStatus(RefereeStatus.PENDING);
        referee.setToken(token);
        referee.setTokenExpiry(tokenService.expiryTime());

        Referee saved = refereeRepository.save(referee);

        refereeEmailService.sendInvitation(saved);

        eventPublisher.publish(app.getId(), app.getApplicant().getId(), ApplicationStep.REFEREES);

        return modelMapper.map(saved, RefereeDTO.class);
    }

    // ================= GET BY APPLICATION =================
    public List<RefereeDTO> getByApplication(Long applicationId) {

        return refereeRepository.findByApplicationId(applicationId)
                .stream()
                .map(r -> modelMapper.map(r, RefereeDTO.class))
                .toList();
    }

    // ================= TOKEN VALIDATION =================
    public RefereeTokenValidationDTO validateToken(String token) {

        Referee referee = refereeRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        RefereeTokenValidationDTO dto = new RefereeTokenValidationDTO();

        // expiry check
        if (referee.getTokenExpiry() == null ||
                referee.getTokenExpiry().isBefore(LocalDateTime.now())) {

            dto.setValid(false);
            dto.setMessage("Token expired");
            return dto;
        }

        dto.setValid(true);
        dto.setRefereeId(referee.getId());
        dto.setFullName(referee.getFullName());
        dto.setEmail(referee.getEmail());
        dto.setApplicationIndex(referee.getApplication().getApplicationIndexNumber());
        dto.setMessage("Token valid");

        return dto;
    }

    public RefereeDTO update(Long id, RefereeDTO dto) {

        Referee referee = refereeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        Application app = referee.getApplication();

        if (app.isLocked()) {
            throw new RuntimeException("Application is locked");
        }

        // ================= SAFE PATCH =================
        patchEngine.patch(dto, referee);

        // ================= PROTECT SYSTEM FIELDS =================
        referee.setId(referee.getId()); // safety
        referee.setApplication(app); // prevent overwrite
        referee.setToken(referee.getToken());
        referee.setStatus(referee.getStatus());

        Referee saved = refereeRepository.save(referee);

        // ================= EVENT =================
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep());

        return modelMapper.map(saved, RefereeDTO.class);
    }
}
// FOR EACH Application → 2 referees
// {
// "fullName": "Juma Salum",
// "title": "Senior Administration Officer",
// "organization": "Zanzibar Revenue Board",
// "email": "juma.salum@zrb.go.tz",
// "phone": "0242234567",
// "relationship": "Direct Supervisor",
// "applicationId": 1
// }
// {
// "fullName": "Fatma Khamis",
// "title": "ICT Manager",
// "organization": "Zanzibar ICT Commission",
// "email": "fatma.khamis@zict.go.tz",
// "phone": "0242239999",
// "relationship": "IT Supervisor",
// "applicationId": 2
// }
// {
// "fullName": "Ali Hassan",
// "title": "Health Records Officer",
// "organization": "Pemba Hospital",
// "email": "ali.hassan@pembahospital.go.tz",
// "phone": "0242456789",
// "relationship": "Department Supervisor",
// "applicationId": 3
// }
// Application 1
// {
// "fullName": "Asha Suleiman",
// "title": "Human Resource Officer",
// "organization": "Zanzibar Revenue Board",
// "email": "asha.suleiman@zrb.go.tz",
// "phone": "0242234500",
// "relationship": "HR Supervisor",
// "applicationId": 1
// }
// Application 2
// {
// "fullName": "Mohamed Rashid",
// "title": "Network Administrator",
// "organization": "Zanzibar ICT Commission",
// "email": "mohamed.rashid@zict.go.tz",
// "phone": "0242238888",
// "relationship": "Technical Supervisor",
// "applicationId": 2
// }
// Application 3
// {
// "fullName": "Salma Omar",
// "title": "Medical Records Manager",
// "organization": "Pemba Hospital",
// "email": "salma.omar@pembahospital.go.tz",
// "phone": "0242456000",
// "relationship": "Line Manager",
// "applicationId": 3
// }
// {
// "fullName": "Asha Suleiman",
// "title": "Human Resource Officer",
// "organization": "Zanzibar Revenue Board",
// "email": "asha.suleiman@zrb.go.tz",
// "phone": "0242234500",
// "relationship": "HR Supervisor",
// "applicationId": 4
// }
// {
// "fullName": "Mohamed Rashid",
// "title": "Network Administrator",
// "organization": "Zanzibar ICT Commission",
// "email": "mohamed.rashid@zict.go.tz",
// "phone": "0242238888",
// "relationship": "Technical Supervisor",
// "applicationId": 4
// }