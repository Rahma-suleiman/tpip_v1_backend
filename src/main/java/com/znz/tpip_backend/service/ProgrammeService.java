package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.*;
import com.znz.tpip_backend.enums.EducationLevel;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

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
//     "name": "Government Administrative Internship",
//     "department": "Public Service",
//     "field": "Administration",
//     "location": "Zanzibar Town",
//     "availableSlots": 20,
//     "durationMonths": 6,
//     "description": "Basic administrative internship for fresh graduates and secondary school leavers to gain office experience.",
//     "requiredLevel": "O_LEVEL"
//   }
//   {
//     "name": "ICT Support Internship - ZICTIA",
//     "department": "Information and Communication Technology",
//     "field": "IT Support",
//     "location": "Stone Town, Zanzibar",
//     "availableSlots": 15,
//     "durationMonths": 6,
//     "description": "Internship for supporting ICT systems, basic troubleshooting, and user support in government offices.",
//     "requiredLevel": "A_LEVEL"
//   }
//   {
//     "name": "Banking Customer Service Internship - PBZ",
//     "department": "Finance",
//     "field": "Banking Operations",
//     "location": "Mwanakwerekwe, Zanzibar",
//     "availableSlots": 10,
//     "durationMonths": 6,
//     "description": "Customer service and front desk banking internship for diploma and secondary graduates.",
//     "requiredLevel": "DIPLOMA"
//   }
//   {
//     "name": "Software Engineering Graduate Trainee - Zantel Tech Hub",
//     "department": "Technology",
//     "field": "Software Engineering",
//     "location": "Stone Town, Zanzibar",
//     "availableSlots": 8,
//     "durationMonths": 12,
//     "description": "Advanced graduate trainee programme focusing on software development and system design.",
//     "requiredLevel": "BACHELORS"
//   }
//   {
//     "name": "Public Health Research Assistant Programme",
//     "department": "Health",
//     "field": "Research",
//     "location": "Mnazi Mmoja Hospital, Zanzibar",
//     "availableSlots": 5,
//     "durationMonths": 12,
//     "description": "Research-based programme for postgraduate students assisting in public health studies and data analysis.",
//     "requiredLevel": "MASTERS"
//   }
