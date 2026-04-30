package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.ProgrammeChoiceDto;
import com.znz.tpip_backend.enums.EducationLevel;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.*;
import com.znz.tpip_backend.validation.ProgrammeChoiceValidator;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgrammeChoiceService {

        private final ProgrammeChoiceRepository programmeChoiceRepository;
        private final ApplicationRepository applicationRepository;
        private final ProgrammeRepository programmeRepository;
        private final EducationRepository educationRepository;
        private final ApplicationWorkflowService workflowService;
        private final ModelMapper modelMapper;

        // ================= CREATE =================
        public ProgrammeChoiceDto create(ProgrammeChoiceDto dto) {

                Application app = applicationRepository.findById(dto.getApplicationId())
                                .orElseThrow(() -> new RuntimeException("Application not found"));

                Programme programme = programmeRepository.findById(dto.getProgrammeId())
                                .orElseThrow(() -> new RuntimeException("Programme not found"));

                List<ProgrammeChoice> existing = programmeChoiceRepository.findByApplicationId(app.getId());

                ProgrammeChoiceValidator.validateCreate(
                                existing,
                                dto.getPreferenceRank(),
                                dto.getProgrammeId());

                ProgrammeChoice choice = new ProgrammeChoice();
                choice.setApplication(app);
                choice.setProgramme(programme);
                choice.setPreferenceRank(dto.getPreferenceRank());

                applyEligibility(choice, app.getApplicant(), programme);
                ProgrammeChoice saved = programmeChoiceRepository.save(choice);

                // AUTO STEP ADVANCE
                workflowService.evaluateAndAdvance(app.getId());

                return map(saved);
        }

        // ================= UPDATE =================
        public ProgrammeChoiceDto update(Long id, ProgrammeChoiceDto dto) {

                ProgrammeChoice choice = programmeChoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Not found"));

                Programme programme = programmeRepository.findById(dto.getProgrammeId())
                                .orElseThrow(() -> new RuntimeException("Programme not found"));

                List<ProgrammeChoice> existing = programmeChoiceRepository
                                .findByApplicationId(choice.getApplication().getId());

                ProgrammeChoiceValidator.validateUpdate(
                                existing,
                                dto.getPreferenceRank(),
                                dto.getProgrammeId(),
                                id);

                choice.setProgramme(programme);
                choice.setPreferenceRank(dto.getPreferenceRank());

                applyEligibility(choice, choice.getApplication().getApplicant(), programme);

                return map(programmeChoiceRepository.save(choice));
        }

        // ================= GET =================
        public List<ProgrammeChoiceDto> getByApplication(Long applicationId) {
                return programmeChoiceRepository.findByApplicationId(applicationId)
                                .stream()
                                .map(this::map)
                                .toList();
        }

        // ================= DELETE =================
        public void delete(Long id) {
                programmeChoiceRepository.deleteById(id);
        }

        // ================= ELIGIBILITY =================
        private void applyEligibility(
                        ProgrammeChoice choice,
                        Applicant applicant,
                        Programme programme) {

                List<Education> edu = educationRepository.findByApplicantId(applicant.getId());

                if (edu.isEmpty()) {
                        choice.setIsEligible(false);
                        choice.setMatchScore(0);
                        choice.setEligibilityRemark("No education found");
                        return;
                }

                Education highest = getHighestEducation(edu);

                boolean eligible = highest.getLevel().ordinal() >= programme.getRequiredLevel().ordinal();

                choice.setIsEligible(eligible);
                choice.setMatchScore(eligible ? 80 : 40);
                choice.setEligibilityRemark(
                                eligible ? "Eligible" : "Not eligible");
        }

        private Education getHighestEducation(List<Education> educations) {

                List<EducationLevel> hierarchy = List.of(
                                EducationLevel.O_LEVEL,
                                EducationLevel.A_LEVEL,
                                EducationLevel.DIPLOMA,
                                EducationLevel.BACHELORS,
                                EducationLevel.MASTERS,
                                EducationLevel.PHD);

                return educations.stream()
                                .max(Comparator.comparingInt(e -> hierarchy.indexOf(e.getLevel())))
                                .orElseThrow(() -> new RuntimeException("No education found"));
        }

        // ================= MAPPER =================
        private ProgrammeChoiceDto map(ProgrammeChoice c) {

                ProgrammeChoiceDto dto = modelMapper.map(c, ProgrammeChoiceDto.class);
                dto.setApplicationId(c.getApplication().getId());
                dto.setProgrammeId(c.getProgramme().getId());
                return dto;
        }
}
// 3 CHOICES FOR APPLICANT 1
// {
// "preferenceRank": 1,
// "matchScore": 85,
// "isEligible": true,
// "eligibilityRemark": "Meets O-Level requirement",
// "applicationId": 1,
// "programmeId": 3
// }
// {
// "preferenceRank": 3,
// "matchScore": 78,
// "isEligible": true,
// "eligibilityRemark": "Meets minimum requirement",
// "applicationId": 1,
// "programmeId": 2
// }
// TEST MAX 3 LIMIT:THIS shld fail(bcz Maximum 3 programme choices allowed i.e
// preference rank allowed 1-3 and this payload is 4)
// {
// "preferenceRank": 4,
// "matchScore": 70,
// "isEligible": true,
// "eligibilityRemark": "Extra choice beyond limit",
// "applicationId": 1,
// "programmeId": 2
// }
// TEST DUPLICATE PROGRAMME:THIS shld fail(bcz duplicate programme not allowed
// i.e programme id 3 has already been choosen for this applicant)
// {
// "preferenceRank": 2,
// "matchScore": 80,
// "isEligible": true,
// "eligibilityRemark": "Duplicate programme test",
// "applicationId": 1,
// "programmeId": 3
// }
// TEST DUPLICATE RANK (also fail,rank already choosen)
// {
// "preferenceRank": 1,
// "matchScore": 75,
// "isEligible": true,
// "eligibilityRemark": "Duplicate rank test",
// "applicationId": 1,
// "programmeId": 2
// }
// TEST INELIGIBLE APPLICANT
// {
// "preferenceRank": 2,
// "matchScore": 95,
// "isEligible": false,
// "eligibilityRemark": "Should be rejected due to level mismatch",
// "applicationId": 1,
// "programmeId": 1
// }
// 3 CHOICES FOR APPLICANT 2

// {
// "preferenceRank": 1,
// "matchScore": 92,
// "isEligible": true,
// "eligibilityRemark": "Meets A-Level requirement",
// "applicationId": 2,
// "programmeId": 1
// }
// {
// "preferenceRank": 2,
// "matchScore": 85,
// "isEligible": true,
// "eligibilityRemark": "Eligible - close field match",
// "applicationId": 2,
// "programmeId": 2
// }
// {
// "preferenceRank": 3,
// "matchScore": 78,
// "isEligible": true,
// "eligibilityRemark": "Eligible - minimum requirement met",
// "applicationId": 2,
// "programmeId": 3
// }
// 3 CHOICES FOR APPLICANT 3

// {
// "preferenceRank": 1,
// "matchScore": 88,
// "isEligible": true,
// "eligibilityRemark": "Meets Diploma requirement",
// "applicationId": 3,
// "programmeId": 2
// }
// {
// "preferenceRank": 2,
// "matchScore": 80,
// "isEligible": true,
// "eligibilityRemark": "Eligible - related field",
// "applicationId": 3,
// "programmeId": 1
// }
// {
// "preferenceRank": 3,
// "matchScore": 70,
// "isEligible": false,
// "eligibilityRemark": "Not eligible - below required level",
// "applicationId": 3,
// "programmeId": 3
// }