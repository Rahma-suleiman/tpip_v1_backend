package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.ProgrammeChoiceDto;
import com.znz.tpip_backend.enums.ApplicationStep;
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
        private final ApplicationEventPublisherService eventPublisher;
        private final ModelMapper modelMapper;

        // ================= CREATE =================
        // public ProgrammeChoiceDto create(ProgrammeChoiceDto dto) {

        // Application app = applicationRepository.findById(dto.getApplicationId())
        // .orElseThrow(() -> new RuntimeException("Application not found"));

        // Programme programme = programmeRepository.findById(dto.getProgrammeId())
        // .orElseThrow(() -> new RuntimeException("Programme not found"));

        // List<ProgrammeChoice> existing =
        // programmeChoiceRepository.findByApplicationId(app.getId());

        // ProgrammeChoiceValidator.validateCreate(
        // existing,
        // dto.getPreferenceRank(),
        // dto.getProgrammeId());

        // ProgrammeChoice choice = new ProgrammeChoice();
        // choice.setApplication(app);
        // choice.setProgramme(programme);
        // choice.setPreferenceRank(dto.getPreferenceRank());

        // applyEligibility(choice, app.getApplicant(), programme);
        // ProgrammeChoice saved = programmeChoiceRepository.save(choice);

        // // ✅ EVENT
        // eventPublisher.publish(app.getId(), app.getApplicant().getId(),
        // ApplicationStep.PROGRAMME_CHOICE);

        // return map(saved);

        // }
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

                // 🔥 SYSTEM ALWAYS COMPUTES THESE
                applyEligibility(choice, app.getApplicant(), programme);

                ProgrammeChoice saved = programmeChoiceRepository.save(choice);

                // eventPublisher.publish(app.getId(), app.getApplicant().getId(), ApplicationStep.PROGRAMME_CHOICE);
                if (app.getCurrentStep() == ApplicationStep.PROGRAMME_CHOICE) {
                        // eventPublisher.publish(app.getId(), app.getApplicant().getId(),ApplicationStep.PROGRAMME_CHOICE);
                        eventPublisher.publish(app.getId(), app.getApplicant().getId(),ApplicationStep.PROGRAMME_CHOICE);
                }

                return map(saved);
        }

        // ================= UPDATE =================
        // public ProgrammeChoiceDto update(Long id, ProgrammeChoiceDto dto) {

        // ProgrammeChoice choice = programmeChoiceRepository.findById(id)
        // .orElseThrow(() -> new RuntimeException("Not found"));

        // Programme programme = programmeRepository.findById(dto.getProgrammeId())
        // .orElseThrow(() -> new RuntimeException("Programme not found"));

        // List<ProgrammeChoice> existing = programmeChoiceRepository
        // .findByApplicationId(choice.getApplication().getId());

        // ProgrammeChoiceValidator.validateUpdate(
        // existing,
        // dto.getPreferenceRank(),
        // dto.getProgrammeId(),
        // id);

        // choice.setProgramme(programme);
        // choice.setPreferenceRank(dto.getPreferenceRank());

        // applyEligibility(choice, choice.getApplication().getApplicant(), programme);

        // ProgrammeChoice saved = programmeChoiceRepository.save(choice);

        // Application app = choice.getApplication();

        // // ✅ EVENT
        // eventPublisher.publish(app.getId(), app.getApplicant().getId(),
        // ApplicationStep.PROGRAMME_CHOICE);

        // return map(saved);
        // // return map(programmeChoiceRepository.save(choice));
        // }

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

                // 🔥 ALWAYS RECALCULATE
                applyEligibility(choice,
                                choice.getApplication().getApplicant(),
                                programme);

                ProgrammeChoice saved = programmeChoiceRepository.save(choice);

                eventPublisher.publish(
                                choice.getApplication().getId(),
                                choice.getApplication().getApplicant().getId(),
                                ApplicationStep.PROGRAMME_CHOICE);

                                
                return map(saved);
        }

        // ================= GET =================
        public List<ProgrammeChoiceDto> getByApplication(Long applicationId) {
                return programmeChoiceRepository.findByApplicationId(applicationId)
                                .stream()
                                .map(this::map)
                                .toList();
        }

        // ================= DELETE =================
        // public void delete(Long id) {
        // programmeChoiceRepository.deleteById(id);
        // }

        public void delete(Long id) {

                ProgrammeChoice choice = programmeChoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Not found"));

                Application app = choice.getApplication();

                programmeChoiceRepository.deleteById(id);

                // ✅ EVENT
                eventPublisher.publish(app.getId(), app.getApplicant().getId(), ApplicationStep.PROGRAMME_CHOICE);
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
// "applicationId": 1,
// "programmeId": 1
// }
// TEST ELIGIBILITY(APPLICANT 1)
// {
// "preferenceRank": 2,
// "applicationId": 1,
// "programmeId": 4
// }
// {
// "preferenceRank": 3,
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

// 3 CHOICES FOR APPLICANT 2
// {
// "preferenceRank": 1,
// "applicationId": 2,
// "programmeId": 2
// }
// TEST ELIGIBILITY(APPLICANT 2):but it will b saved and rank will exist
// {
// "preferenceRank": 2,
// "applicationId": 2,
// "programmeId": 5
// }
// TEST DUPLICATE PROGRAMME:THIS shld fail(bcz duplicate programme not allowed)
// {
// "preferenceRank": 3,
// "applicationId": 2,
// "programmeId": 2
// }
// {
// "preferenceRank": 3,
// "applicationId": 2,
// "programmeId": 3
// }
// TEST MAX 3 LIMIT:THIS shld fail(bcz Maximum 3 programme choices allowed i.e
// preference rank allowed 1-3 and this payload is 4)
// {
// "preferenceRank": 4,
// "applicationId": 2,
// "programmeId": 4
// }

// 3 CHOICES FOR APPLICANT 3
// {
// "preferenceRank": 1,
// "applicationId": 3,
// "programmeId": 3
// }
//
// TEST ELIGIBILITY(APPLICANT 3):but it will b saved
// {
// "preferenceRank": 2,
// "applicationId": 3,
// "programmeId": 5
// }
// {
// "preferenceRank": 3,
// "applicationId": 3,
// "programmeId": 2
// }
