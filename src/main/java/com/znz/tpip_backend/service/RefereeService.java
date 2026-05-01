// package com.znz.tpip_backend.service;

// import com.znz.tpip_backend.dto.RefereeDTO;
// // import com.znz.tpip_backend.enums.ApplicationStep;
// import com.znz.tpip_backend.enums.RefereeStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Referee;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.repository.RefereeRepository;
// import lombok.RequiredArgsConstructor;
// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import java.util.List;
// @Service
// @RequiredArgsConstructor
// public class RefereeService {

//     private final RefereeRepository refereeRepository;
//     private final ApplicationRepository applicationRepository;
//     private final ModelMapper modelMapper;
//     private final ApplicationEventPublisherService eventPublisher;

//     public RefereeDTO create(RefereeDTO dto) {

//         Application app = applicationRepository.findById(dto.getApplicationId())
//                 .orElseThrow(() -> new RuntimeException("Application not found"));

//         Referee referee = modelMapper.map(dto, Referee.class);
//         referee.setApplication(app);
//         referee.setStatus(RefereeStatus.PENDING);

//         Referee saved = refereeRepository.save(referee);

//         // 🔥 EVENT TRIGGER
//         eventPublisher.publish(
//                 app.getId(),
//                 app.getApplicant().getId(),
//                 app.getCurrentStep()
//         );

//         return modelMapper.map(saved, RefereeDTO.class);
//     }

//     public List<RefereeDTO> getByApplication(Long applicationId) {

//         return refereeRepository.findByApplicationId(applicationId)
//                 .stream()
//                 .map(r -> modelMapper.map(r, RefereeDTO.class))
//                 .toList();
//     }
// }

// package com.znz.tpip_backend.service;

// import com.znz.tpip_backend.dto.RefereeDTO;
// import com.znz.tpip_backend.email.RefereeEmailService;
// import com.znz.tpip_backend.enums.RefereeStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Referee;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.repository.RefereeRepository;
// import lombok.RequiredArgsConstructor;
// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// @RequiredArgsConstructor
// public class RefereeService {

//     private final RefereeRepository refereeRepository;
//     private final ApplicationRepository applicationRepository;
//     private final ModelMapper modelMapper;
//     private final ApplicationEventPublisherService eventPublisher;
//     private final RefereeEmailService refereeEmailService;

//     public RefereeDTO create(RefereeDTO dto) {

//         Application app = applicationRepository.findById(dto.getApplicationId())
//                 .orElseThrow(() -> new RuntimeException("Application not found"));

//         Referee referee = modelMapper.map(dto, Referee.class);
//         referee.setApplication(app);
//         referee.setStatus(RefereeStatus.PENDING);

//         Referee saved = refereeRepository.save(referee);

//         // SEND EMAIL INVITATION
//         refereeEmailService.sendInvitation(saved);

//         // EVENT TRIGGER
//         eventPublisher.publish(
//                 app.getId(),
//                 app.getApplicant().getId(),
//                 app.getCurrentStep()
//         );

//         return modelMapper.map(saved, RefereeDTO.class);
//     }

//     public List<RefereeDTO> getByApplication(Long applicationId) {

//         return refereeRepository.findByApplicationId(applicationId)
//                 .stream()
//                 .map(r -> modelMapper.map(r, RefereeDTO.class))
//                 .toList();
//     }
// }


// package com.znz.tpip_backend.service;

// import com.znz.tpip_backend.dto.RefereeDTO;
// import com.znz.tpip_backend.dto.RefereeTokenValidationDTO;
// import com.znz.tpip_backend.email.RefereeEmailService;
// import com.znz.tpip_backend.enums.RefereeStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Referee;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.repository.RefereeRepository;
// import lombok.RequiredArgsConstructor;
// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.List;

// @Service
// @RequiredArgsConstructor
// public class RefereeService {

//     private final RefereeRepository refereeRepository;
//     private final ApplicationRepository applicationRepository;
//     private final ModelMapper modelMapper;
//     private final ApplicationEventPublisherService eventPublisher;
//     private final RefereeEmailService refereeEmailService;

//     public RefereeDTO create(RefereeDTO dto) {

//         Application app = applicationRepository.findById(dto.getApplicationId())
//                 .orElseThrow(() -> new RuntimeException("Application not found"));

//         Referee referee = modelMapper.map(dto, Referee.class);
//         referee.setApplication(app);
//         referee.setStatus(RefereeStatus.PENDING);

//         Referee saved = refereeRepository.save(referee);

//         // 📧 EMAIL INVITATION
//         refereeEmailService.sendInvitation(saved);

//         // 🔥 EVENT TRIGGER
//         eventPublisher.publish(
//                 app.getId(),
//                 app.getApplicant().getId(),
//                 app.getCurrentStep());

//         return modelMapper.map(saved, RefereeDTO.class);
//     }

//     public List<RefereeDTO> getByApplication(Long applicationId) {

//         return refereeRepository.findByApplicationId(applicationId)
//                 .stream()
//                 .map(r -> modelMapper.map(r, RefereeDTO.class))
//                 .toList();
//     }

//     public RefereeTokenValidationDTO validateToken(String token) {

//         Referee referee = refereeRepository.findByToken(token)
//                 .orElseThrow(() -> new RuntimeException("Invalid token"));

//         RefereeTokenValidationDTO dto = new RefereeTokenValidationDTO();

//         // check expiry
//         if (referee.getTokenExpiry() == null ||
//                 referee.getTokenExpiry().isBefore(LocalDateTime.now())) {

//             dto.setValid(false);
//             dto.setMessage("Token expired");
//             return dto;
//         }

//         // valid token
//         dto.setValid(true);
//         dto.setRefereeId(referee.getId());
//         dto.setFullName(referee.getFullName());
//         dto.setEmail(referee.getEmail());
//         dto.setApplicationIndex(referee.getApplication().getIndexNumber());
//         dto.setMessage("Token valid");

//         return dto;
//     }

// }

package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.RefereeDTO;
import com.znz.tpip_backend.dto.RefereeTokenValidationDTO;
import com.znz.tpip_backend.email.RefereeEmailService;
import com.znz.tpip_backend.enums.RefereeStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Referee;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.RefereeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefereeService {

    private final RefereeRepository refereeRepository;
    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisherService eventPublisher;
    private final RefereeEmailService refereeEmailService;
    private final TokenService tokenService;

    // ================= CREATE REFEREE =================
    public RefereeDTO create(RefereeDTO dto) {

        Application app = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Referee referee = modelMapper.map(dto, Referee.class);

        // ================= TOKEN MUST BE GENERATED HERE =================
        String token = tokenService.generateToken();

        referee.setApplication(app);
        referee.setStatus(RefereeStatus.PENDING);
        referee.setToken(token);
        referee.setTokenExpiry(tokenService.expiryTime());

        // ================= SAVE FIRST (IMPORTANT FIX) =================
        Referee saved = refereeRepository.save(referee);

        // ================= SEND EMAIL USING SAVED DATA =================
        refereeEmailService.sendInvitation(saved);

        // ================= TRIGGER WORKFLOW =================
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep()
        );

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
        dto.setApplicationIndex(referee.getApplication().getIndexNumber());
        dto.setMessage("Token valid");

        return dto;
    }
}
// {
//   "fullName": "Juma Salum",
//   "title": "Senior Administration Officer",
//   "organization": "Zanzibar Revenue Board",
//   "email": "juma.salum@zrb.go.tz",
//   "phone": "0242234567",
//   "relationship": "Direct Supervisor",
//   "applicationId": 1
// }
// {
//   "fullName": "Fatma Khamis",
//   "title": "ICT Manager",
//   "organization": "Zanzibar ICT Commission",
//   "email": "fatma.khamis@zict.go.tz",
//   "phone": "0242239999",
//   "relationship": "IT Supervisor",
//   "applicationId": 2
// }
// {
//   "fullName": "Ali Hassan",
//   "title": "Health Records Officer",
//   "organization": "Pemba Hospital",
//   "email": "ali.hassan@pembahospital.go.tz",
//   "phone": "0242456789",
//   "relationship": "Department Supervisor",
//   "applicationId": 3
// }