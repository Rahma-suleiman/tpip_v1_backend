// package com.znz.tpip_backend.service;

// import com.znz.tpip_backend.dto.*;
// import com.znz.tpip_backend.enums.EducationLevel;
// import com.znz.tpip_backend.model.*;
// import com.znz.tpip_backend.repository.*;

// import lombok.RequiredArgsConstructor;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// public class ProgrammeService {

//     private final ProgrammeRepository programmeRepository;
//     private final ProgrammeChoiceRepository choiceRepository;
//     private final ApplicationRepository applicationRepository;
//     private final EducationRepository educationRepository;
//     private final ModelMapper modelMapper;

//     public List<Programme> getAllProgrammes() {
//         return programmeRepository.findByIsActiveTrue();
//     }

//     public List<Programme> filterProgrammes(String department, String field, String location) {

//         return programmeRepository.findAll().stream()
//                 .filter(p -> p.getIsActive())
//                 .filter(p -> department == null || p.getDepartment().equalsIgnoreCase(department))
//                 .filter(p -> field == null || p.getField().equalsIgnoreCase(field))
//                 .filter(p -> location == null || p.getLocation().equalsIgnoreCase(location))
//                 .toList();
//     }

//     // ================= SMART RECOMMENDATION =================
//     public List<ProgrammeRecommendationDto> recommendProgrammes(Long applicantId) {

//         List<Programme> programmes = programmeRepository.findByIsActiveTrue();
//         List<Education> educations = educationRepository.findByApplicantId(applicantId);

//         if (educations.isEmpty()) {
//             throw new RuntimeException("No education data found");
//         }

//         Education highest = getHighestEducation(educations);

//         return programmes.stream()
//                 .map(p -> scoreProgramme(p, highest))
//                 .sorted((a, b) -> b.getMatchScore() - a.getMatchScore())
//                 .limit(5)
//                 .collect(Collectors.toList());
//     }

//     // ================= SAVE PROGRAMME CHOICE =================
//     public ProgrammeChoiceDto chooseProgramme(ProgrammeChoiceDto dto) {

//         Application application = applicationRepository.findById(dto.getApplicationId())
//                 .orElseThrow(() -> new RuntimeException("Application not found"));

//         Programme programme = programmeRepository.findById(dto.getProgrammeId())
//                 .orElseThrow(() -> new RuntimeException("Programme not found"));

//         List<Education> educations = educationRepository.findByApplicantId(
//                 application.getApplicant().getId());

//         Education highest = getHighestEducation(educations);

//         ProgrammeRecommendationDto scored = scoreProgramme(programme, highest);

//         ProgrammeChoice choice = ProgrammeChoice.builder()
//                 .application(application)
//                 .programme(programme)
//                 .preferenceRank(dto.getPreferenceRank())
//                 .matchScore(scored.getMatchScore())
//                 .isEligible(scored.getMatchScore() >= 50)
//                 .eligibilityRemark(scored.getReason())
//                 .build();

//         return mapToDto(choiceRepository.save(choice));
//     }

//     // ================= SCORING ENGINE =================
//     private ProgrammeRecommendationDto scoreProgramme(Programme programme, Education edu) {

//         int score = 0;
//         StringBuilder reason = new StringBuilder();

//         // ===== LEVEL MATCH =====
//         if (edu.getLevel() == programme.getRequiredLevel()) {
//             score += 50;
//             reason.append("Matches your education level. ");
//         } else if (isCloseLevel(edu.getLevel(), programme.getRequiredLevel())) {
//             score += 30;
//             reason.append("Slightly below required level. ");
//         }

//         // ===== GPA =====
//         if (edu.getGpa() != null) {
//             try {
//                 double gpa = Double.parseDouble(edu.getGpa());
//                 if (gpa >= 3.5) {
//                     score += 20;
//                     reason.append("Strong GPA. ");
//                 }
//             } catch (Exception ignored) {
//             }
//         }

//         // ===== FIELD MATCH =====
//         if (programme.getField() != null &&
//                 edu.getProgrammeName() != null &&
//                 edu.getProgrammeName().toLowerCase()
//                         .contains(programme.getField().toLowerCase())) {

//             score += 20;
//             reason.append("Relevant field match. ");
//         }

//         ProgrammeRecommendationDto dto = new ProgrammeRecommendationDto();
//         dto.setProgrammeId(programme.getId());
//         dto.setProgrammeName(programme.getName());
//         dto.setMatchScore(score);
//         dto.setReason(reason.toString());

//         return dto;
//     }

//     // ================= LEVEL HELPER =================
//     private boolean isCloseLevel(EducationLevel userLevel, EducationLevel requiredLevel) {

//         List<EducationLevel> levels = List.of(
//                 EducationLevel.O_LEVEL,
//                 EducationLevel.A_LEVEL,
//                 EducationLevel.DIPLOMA,
//                 EducationLevel.BACHELORS,
//                 EducationLevel.MASTERS,
//                 EducationLevel.PHD);

//         int userIndex = levels.indexOf(userLevel);
//         int requiredIndex = levels.indexOf(requiredLevel);

//         return userIndex >= 0 && requiredIndex >= 0 && userIndex + 1 == requiredIndex;
//     }

//     // ================= GET HIGHEST EDUCATION =================
//     private Education getHighestEducation(List<Education> educations) {

//         List<EducationLevel> hierarchy = List.of(
//                 EducationLevel.O_LEVEL,
//                 EducationLevel.A_LEVEL,
//                 EducationLevel.DIPLOMA,
//                 EducationLevel.BACHELORS,
//                 EducationLevel.MASTERS,
//                 EducationLevel.PHD);

//         return educations.stream()
//                 .max(Comparator.comparingInt(e -> hierarchy.indexOf(e.getLevel())))
//                 .orElseThrow(() -> new RuntimeException("No education found"));
//     }

//     // ================= MAPPER =================
//     private ProgrammeChoiceDto mapToDto(ProgrammeChoice choice) {

//         ProgrammeChoiceDto dto = new ProgrammeChoiceDto();

//         dto.setId(choice.getId());
//         dto.setApplicationId(choice.getApplication().getId());
//         dto.setProgrammeId(choice.getProgramme().getId());
//         dto.setPreferenceRank(choice.getPreferenceRank());
//         dto.setMatchScore(choice.getMatchScore());
//         dto.setIsEligible(choice.getIsEligible());
//         dto.setEligibilityRemark(choice.getEligibilityRemark());

//         return dto;
//     }

//     // =================ADMIN CREATE =================
//     public ProgrammeDto createProgramme(ProgrammeDto dto) {

//         Programme programme = modelMapper.map(dto, Programme.class);

//         // convert requiredLevel (String → Enum)
//         programme.setRequiredLevel(
//                 EducationLevel.valueOf(dto.getRequiredLevel()));

//         return modelMapper.map(programmeRepository.save(programme), ProgrammeDto.class);
//     }

//     // =================ADMIN UPDATE =================
//     public ProgrammeDto updateProgramme(Long id, ProgrammeDto dto) {

//         Programme programme = programmeRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Programme not found"));

//         modelMapper.map(dto, programme);

//         programme.setRequiredLevel(
//                 EducationLevel.valueOf(dto.getRequiredLevel()));

//         return modelMapper.map(programmeRepository.save(programme), ProgrammeDto.class);
//     }

//     // =================ADMIN DELETE =================
//     public void deleteProgramme(Long id) {
//         programmeRepository.deleteById(id);
//     }
// }
package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.*;
import com.znz.tpip_backend.enums.EducationLevel;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgrammeService {

    private final ProgrammeRepository programmeRepository;
    private final EducationRepository educationRepository;
    private final ModelMapper modelMapper;

    // ================= GET ALL =================
    public List<Programme> getAllProgrammes() {
        return programmeRepository.findByIsActiveTrue();
    }

    // ================= FILTER =================
    public List<Programme> filterProgrammes(String department, String field, String location) {

        return programmeRepository.findAll().stream()
                .filter(Programme::getIsActive)
                .filter(p -> department == null || p.getDepartment().equalsIgnoreCase(department))
                .filter(p -> field == null || p.getField().equalsIgnoreCase(field))
                .filter(p -> location == null || p.getLocation().equalsIgnoreCase(location))
                .toList();
    }

    // ================= RECOMMENDATIONS =================
    public List<ProgrammeRecommendationDto> recommendProgrammes(Long applicantId) {

        List<Programme> programmes = programmeRepository.findByIsActiveTrue();
        List<Education> educations = educationRepository.findByApplicantId(applicantId);

        if (educations.isEmpty()) {
            throw new IllegalStateException("No education data found");
        }

        Education highest = getHighestEducation(educations);

        return programmes.stream()
                .map(p -> score(p, highest))
                .sorted((a, b) -> b.getMatchScore() - a.getMatchScore())
                .limit(5)
                .toList();
    }

    // ================= SCORING =================
    private ProgrammeRecommendationDto score(Programme programme, Education edu) {

        int score = 0;
        String reason = "";

        if (edu.getLevel() == programme.getRequiredLevel()) {
            score += 50;
            reason += "Level match. ";
        } else if (isClose(edu.getLevel(), programme.getRequiredLevel())) {
            score += 30;
            reason += "Close level. ";
        }

        if (edu.getProgrammeName() != null &&
                programme.getField() != null &&
                edu.getProgrammeName().toLowerCase()
                        .contains(programme.getField().toLowerCase())) {

            score += 20;
            reason += "Field match. ";
        }

        ProgrammeRecommendationDto dto = new ProgrammeRecommendationDto();
        dto.setProgrammeId(programme.getId());
        dto.setProgrammeName(programme.getName());
        dto.setMatchScore(score);
        dto.setReason(reason);

        return dto;
    }

    // ================= LEVEL CHECK =================
    private boolean isClose(EducationLevel user, EducationLevel required) {

        List<EducationLevel> levels = List.of(
                EducationLevel.O_LEVEL,
                EducationLevel.A_LEVEL,
                EducationLevel.DIPLOMA,
                EducationLevel.BACHELORS,
                EducationLevel.MASTERS,
                EducationLevel.PHD
        );

        return levels.indexOf(user) + 1 == levels.indexOf(required);
    }

    // ================= HIGHEST =================
    private Education getHighestEducation(List<Education> list) {

        List<EducationLevel> levels = List.of(
                EducationLevel.O_LEVEL,
                EducationLevel.A_LEVEL,
                EducationLevel.DIPLOMA,
                EducationLevel.BACHELORS,
                EducationLevel.MASTERS,
                EducationLevel.PHD
        );

        return list.stream()
                .max(Comparator.comparingInt(e -> levels.indexOf(e.getLevel())))
                .orElseThrow();
    }

    // ================= ADMIN =================
    public ProgrammeDto createProgramme(ProgrammeDto dto) {
        Programme p = modelMapper.map(dto, Programme.class);
        return modelMapper.map(programmeRepository.save(p), ProgrammeDto.class);
    }

    public ProgrammeDto updateProgramme(Long id, ProgrammeDto dto) {

        Programme p = programmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        modelMapper.map(dto, p);
        return modelMapper.map(programmeRepository.save(p), ProgrammeDto.class);
    }

    public void deleteProgramme(Long id) {
        programmeRepository.deleteById(id);
    }
}
//   {
//     "name": "Computer Science",
//     "department": "IT",
//     "field": "Software Engineering",
//     "location": "Main Campus",
//     "availableSlots": 50,
//     "durationMonths": 36,
//     "description": "Software development program",
//     "requiredLevel": "A_LEVEL"
//   },
//   {
//     "name": "Business Administration",
//     "department": "Business",
//     "field": "Management",
//     "location": "City Campus",
//     "availableSlots": 40,
//     "durationMonths": 36,
//     "description": "Business management program",
//     "requiredLevel": "A_LEVEL"
//   }
//   {
//     "name": "Information Technology",
//     "department": "IT",
//     "field": "Networking",
//     "location": "Main Campus",
//     "availableSlots": 60,
//     "durationMonths": 24,
//     "description": "IT networking program",
//     "requiredLevel": "O_LEVEL"
//   }
