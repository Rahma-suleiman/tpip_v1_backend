package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.*;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.EducationLevel;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.*;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationEventPublisherService;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationStepGuard;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationStepGuard stepGuard;
    private final ApplicationEventPublisherService eventPublisher;
    private final ModelMapper modelMapper;

    // ================= CREATE =================
    public EducationDto create(EducationDto dto) {

        validateEducation(dto);

        Applicant applicant = applicantRepository.findById(dto.getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        Application app = getApplication(applicant.getId());

        // ✅ STEP GUARD
        stepGuard.validateStep(app, ApplicationStep.EDUCATION);

        Education education = modelMapper.map(dto, Education.class);
        education.setApplicant(applicant);

        mapSubjects(dto, education);

        Education saved = educationRepository.save(education);

        eventPublisher.publish(app.getId(), applicant.getId(), ApplicationStep.EDUCATION);

        return mapToDto(saved);
    }

    // ================= UPDATE =================
    public EducationDto update(Long id, EducationDto dto) {

        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        Application app = getApplication(education.getApplicant().getId());

        stepGuard.validateStep(app, ApplicationStep.EDUCATION);

        if (dto.getLevel() != null)
            education.setLevel(dto.getLevel());
        if (dto.getInstitutionName() != null)
            education.setInstitutionName(dto.getInstitutionName());
        if (dto.getProgrammeName() != null)
            education.setProgrammeName(dto.getProgrammeName());
        if (dto.getCompletionYear() != null)
            education.setCompletionYear(dto.getCompletionYear());
        if (dto.getGpa() != null)
            education.setGpa(dto.getGpa());
        if (dto.getClassification() != null)
            education.setClassification(dto.getClassification());

        if (dto.getSubjects() != null) {
            mapSubjects(dto, education);
        }

        validateEducationForUpdate(education);

        Education saved = educationRepository.save(education);

        eventPublisher.publish(app.getId(), education.getApplicant().getId(), ApplicationStep.EDUCATION);

        return mapToDto(saved);
    }

    private void validateEducationForUpdate(Education education) {

        if (education.getLevel() == null) {
            throw new RuntimeException("Education level is required");
        }

        if (education.getInstitutionName() == null || education.getInstitutionName().isBlank()) {
            throw new RuntimeException("Institution name is required");
        }

        if (education.getCompletionYear() == null) {
            throw new RuntimeException("Completion year is required");
        }

        // RULE: O/A Level must have subjects IF subjects exist
        if ((education.getLevel() == EducationLevel.O_LEVEL ||
                education.getLevel() == EducationLevel.A_LEVEL)) {

            if (education.getSubjects() != null && education.getSubjects().isEmpty()) {
                throw new RuntimeException("At least one subject is required");
            }
        }
    }

    // ================= GET =================
    public List<EducationDto> getByApplicant(Long applicantId) {

        return educationRepository.findByApplicantId(applicantId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ================= DELETE =================
    public void delete(Long id) {

        Education edu = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        Application app = getApplication(edu.getApplicant().getId());

        stepGuard.validateStep(app, ApplicationStep.EDUCATION);

        educationRepository.deleteById(id);

        eventPublisher.publish(app.getId(), edu.getApplicant().getId(), ApplicationStep.EDUCATION);
    }

    // ================= APPLICATION FETCH =================
    private Application getApplication(Long applicantId) {
        return applicationRepository
                .findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    // ================= VALIDATION =================
    private void validateEducation(EducationDto dto) {

        if (dto.getLevel() == null) {
            throw new RuntimeException("Education level is required");
        }

        if (dto.getInstitutionName() == null || dto.getInstitutionName().isBlank()) {
            throw new RuntimeException("Institution name is required");
        }

        if (dto.getCompletionYear() == null) {
            throw new RuntimeException("Completion year is required");
        }

        switch (dto.getLevel()) {

            case O_LEVEL:
            case A_LEVEL:

                if (dto.getSubjects() == null || dto.getSubjects().isEmpty()) {
                    throw new RuntimeException(dto.getLevel() + " must have at least one subject");
                }

                if (!isBlank(dto.getGpa()) || !isBlank(dto.getClassification())) {
                    throw new RuntimeException(dto.getLevel() + " should not have GPA or classification");
                }
                break;

            case DIPLOMA:
            case BACHELORS:
            case MASTERS:
            case PHD:

                if (dto.getProgrammeName() == null || dto.getProgrammeName().isBlank()) {
                    throw new IllegalStateException(dto.getLevel() + " requires programme name");
                }

                if (isBlank(dto.getGpa()) && isBlank(dto.getClassification())) {
                    throw new IllegalStateException(dto.getLevel() + " requires GPA or classification");
                }

                if (dto.getSubjects() != null && !dto.getSubjects().isEmpty()) {
                    throw new IllegalStateException(dto.getLevel() + " should not have subjects");
                }
                break;

            case OTHER:
                break;
        }

        if (dto.getSubjects() != null) {
            for (EducationSubjectDto s : dto.getSubjects()) {

                if (s.getSubjectName() == null || s.getSubjectName().isBlank()) {
                    throw new RuntimeException("Subject name is required");
                }

                if (s.getGrade() == null || s.getGrade().isBlank()) {
                    throw new RuntimeException("Grade is required for subject: " + s.getSubjectName());
                }
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // ================= SUBJECT MAPPING =================
    private void mapSubjects(EducationDto dto, Education education) {

        if (education.getSubjects() == null) {
            education.setSubjects(new ArrayList<>());
        }

        education.getSubjects().clear();

        if (dto.getSubjects() != null) {

            for (EducationSubjectDto s : dto.getSubjects()) {

                EducationSubject subject = new EducationSubject();
                subject.setSubjectName(s.getSubjectName());
                subject.setGrade(s.getGrade());
                subject.setEducation(education);

                education.getSubjects().add(subject);
            }
        }
    }

    // ================= MAPPING =================
    private EducationDto mapToDto(Education education) {

        EducationDto dto = modelMapper.map(education, EducationDto.class);

        dto.setApplicantId(education.getApplicant().getId());

        if (education.getSubjects() != null) {
            dto.setSubjects(
                    education.getSubjects().stream()
                            .map(s -> {
                                EducationSubjectDto sd = new EducationSubjectDto();
                                sd.setSubjectName(s.getSubjectName());
                                sd.setGrade(s.getGrade());
                                return sd;
                            }).collect(Collectors.toList()));
        }

        if (education.getDocuments() != null) {
            dto.setDocumentIds(
                    education.getDocuments()
                            .stream()
                            .map(Document::getId)
                            .collect(Collectors.toList()));
        }

        return dto;
    }
}

// {
// "level": "O_LEVEL",
// "institutionName": "Lumumba Secondary School",
// "programmeName": null,
// "completionYear": 2018,
// "gpa": null,
// "classification": null,
// "description": "Form IV Certificate - Zanzibar O-Level Education",
// "applicantId": 1,
// "subjects": [
// { "subjectName": "Mathematics", "grade": "A" },
// { "subjectName": "English", "grade": "B" },
// { "subjectName": "Biology", "grade": "A" },
// { "subjectName": "Civics", "grade": "B+" }
// ]
// }

// {
// "level": "A_LEVEL",
// "institutionName": "Forodhani Secondary School",
// "programmeName": "PCM",
// "completionYear": 2020,
// "gpa": null,
// "classification": null,
// "description": "Advanced Certificate of Secondary Education (ACSEE)",
// "applicantId": 2,
// "subjects": [
// { "subjectName": "Physics", "grade": "A" },
// { "subjectName": "Mathematics", "grade": "A" },
// { "subjectName": "Chemistry", "grade": "B+" }
// ]
// }

// {
// "level": "DIPLOMA",
// "institutionName": "State University of Zanzibar (SUZA)",
// "programmeName": "Information Technology",
// "completionYear": 2023,
// "gpa": "3.9",
// "classification": "FIRST",
// "description": "Diploma in Information Technology - Software and Systems
// Track",
// "applicantId": 3,
// "subjects": []
// }
// {
// "level": "MASTERS",
// "institutionName": "State University of Zanzibar (SUZA)",
// "programmeName": "Information Technology",
// "completionYear": 2025,
// "gpa": "4.2",
// "classification": "DISTINCTION",
// "description": "Master of Science in Information Technology",
// "applicantId": 4,
// "subjects": []
// }