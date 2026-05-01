package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.*;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.*;

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
    // private final ApplicationWorkflowService workflowService;
    private final ApplicationEventPublisherService eventPublisher;
    private final ModelMapper modelMapper;

    // ================= CREATE =================
    public EducationDto create(EducationDto dto) {

        validateEducation(dto);

        Applicant applicant = applicantRepository.findById(dto.getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        Education education = modelMapper.map(dto, Education.class);
        education.setApplicant(applicant);

        mapSubjects(dto, education);

        Education saved = educationRepository.save(education);

        Application app = getApplication(applicant.getId());

        // ✅ EVENT
        eventPublisher.publish(app.getId(), applicant.getId(), ApplicationStep.EDUCATION);

        return mapToDto(saved);
    }

    // ================= UPDATE =================
    public EducationDto update(Long id, EducationDto dto) {

        validateEducation(dto);

        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        modelMapper.map(dto, education);

        mapSubjects(dto, education);

        Education saved = educationRepository.save(education);

        Application app = getApplication(education.getApplicant().getId());

        // ✅ EVENT
        eventPublisher.publish(app.getId(), education.getApplicant().getId(), ApplicationStep.EDUCATION);

        return mapToDto(saved);
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

        Long applicantId = edu.getApplicant().getId();

        educationRepository.deleteById(id);

        Application app = getApplication(applicantId);

        // ✅ EVENT (important)
        eventPublisher.publish(app.getId(), applicantId, ApplicationStep.EDUCATION);
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
                    throw new RuntimeException(dto.getLevel() + " requires programme name");
                }

                if (isBlank(dto.getGpa()) && isBlank(dto.getClassification())) {
                    throw new RuntimeException(dto.getLevel() + " requires GPA or classification");
                }

                if (dto.getSubjects() != null && !dto.getSubjects().isEmpty()) {
                    throw new RuntimeException(dto.getLevel() + " should not have subjects");
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
//   "level": "O_LEVEL",
//   "institutionName": "Lumumba Secondary School",
//   "programmeName": null,
//   "completionYear": 2018,
//   "gpa": null,
//   "classification": null,
//   "description": "Form IV Certificate - Zanzibar O-Level Education",
//   "applicantId": 1,
//   "subjects": [
//     { "subjectName": "Mathematics", "grade": "A" },
//     { "subjectName": "English", "grade": "B" },
//     { "subjectName": "Biology", "grade": "A" },
//     { "subjectName": "Civics", "grade": "B+" }
//   ]
// }

// {
//   "level": "A_LEVEL",
//   "institutionName": "Forodhani Secondary School",
//   "programmeName": "PCM",
//   "completionYear": 2020,
//   "gpa": null,
//   "classification": null,
//   "description": "Advanced Certificate of Secondary Education (ACSEE)",
//   "applicantId": 2,
//   "subjects": [
//     { "subjectName": "Physics", "grade": "A" },
//     { "subjectName": "Mathematics", "grade": "A" },
//     { "subjectName": "Chemistry", "grade": "B+" }
//   ]
// }
// {
//   "level": "DIPLOMA",
//   "institutionName": "State University of Zanzibar (SUZA)",
//   "programmeName": "Information Technology",
//   "completionYear": 2023,
//   "gpa": "3.9",
//   "classification": "FIRST",
//   "description": "Diploma in Information Technology - Software and Systems Track",
//   "applicantId": 3,
//   "subjects": []
// }