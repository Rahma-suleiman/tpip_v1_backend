package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.WorkExperienceDto;
// import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkExperienceService {

    private final WorkExperienceRepository workExperienceRepository;
    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationEventPublisherService eventPublisher;
    private final ModelMapper modelMapper;

    public WorkExperienceDto create(WorkExperienceDto dto) {

        Applicant applicant = applicantRepository.findById(dto.getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        validate(dto);

        WorkExperience work = modelMapper.map(dto, WorkExperience.class);
        work.setApplicant(applicant);

        WorkExperience saved = workExperienceRepository.save(work);

        Application app = getApplication(applicant.getId());

        // ✅ EVENT
        eventPublisher.publish(app.getId(), applicant.getId(), ApplicationStep.WORK_EXPERIENCE);

        return mapToDto(saved);
    }

    public WorkExperienceDto update(Long id, WorkExperienceDto dto) {

        WorkExperience work = workExperienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        validate(dto);

        modelMapper.map(dto, work);

        WorkExperience saved = workExperienceRepository.save(work);

        Application app = getApplication(work.getApplicant().getId());

        // ✅ EVENT
        eventPublisher.publish(app.getId(), work.getApplicant().getId(), ApplicationStep.WORK_EXPERIENCE);

        return mapToDto(saved);
    }

    public List<WorkExperienceDto> getByApplicant(Long applicantId) {

        return workExperienceRepository.findByApplicantId(applicantId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {

        WorkExperience work = workExperienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        Long applicantId = work.getApplicant().getId();

        workExperienceRepository.deleteById(id);

        Application app = getApplication(applicantId);

        // ✅ EVENT
        eventPublisher.publish(app.getId(), applicantId, ApplicationStep.WORK_EXPERIENCE);
    }

    // ================= HELPERS =================

    private Application getApplication(Long applicantId) {
        return applicationRepository
                .findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    private void validate(WorkExperienceDto dto) {

        if (dto.getEmployerName() == null || dto.getEmployerName().isBlank()) {
            throw new RuntimeException("Employer name is required");
        }

        if (dto.getJobTitle() == null || dto.getJobTitle().isBlank()) {
            throw new RuntimeException("Job title is required");
        }

        if (dto.getStartDate() == null) {
            throw new RuntimeException("Start date is required");
        }

        boolean isCurrent = Boolean.TRUE.equals(dto.getIsCurrentlyEmployed());

        if (!isCurrent && dto.getEndDate() == null) {
            throw new RuntimeException("End date is required if not currently employed");
        }

        if (isCurrent && dto.getEndDate() != null) {
            throw new RuntimeException("End date must be null if currently employed");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }
    }

    private WorkExperienceDto mapToDto(WorkExperience work) {

        WorkExperienceDto dto = modelMapper.map(work, WorkExperienceDto.class);
        dto.setApplicantId(work.getApplicant().getId());

        if (work.getDocuments() != null) {
            dto.setDocumentIds(
                    work.getDocuments()
                            .stream()
                            .map(Document::getId)
                            .collect(Collectors.toList()));
        }

        return dto;
    }
}

// {
// "employerName": "Tanzania Revenue Authority",
// "employerAddress": "Dar es Salaam",
// "employerPhone": "255700000001",
// "employerEmail": "hr@tra.go.tz",
// "jobTitle": "Intern",
// "department": "IT",
// "responsibilities": "Data entry and system support",
// "startDate": "2023-01-01",
// "endDate": "2023-06-01",
// "isCurrentlyEmployed": false,
// "employmentType": "FULL_TIME",
// "country": "Tanzania",
// "region": "URBAN_WEST",
// "district": "MJINI",
// "city": "Zanzibar",
// "applicantId": 1
// }
// {
// "employerName": "NMB Bank",
// "employerAddress": "Samora Avenue",
// "employerPhone": "255700000002",
// "employerEmail": "hr@nmb.co.tz",
// "jobTitle": "Assistant",
// "department": "Finance",
// "responsibilities": "Customer support",
// "startDate": "2022-03-01",
// "endDate": "2022-12-01",
// "isCurrentlyEmployed": false,
// "employmentType": "PART_TIME",
// "country": "Tanzania",
// "region": "URBAN_WEST",
// "district": "KUSINI",
// "city": "Zanzibar",
// "applicantId": 2
// }
// {
// "employerName": "Vodacom Tanzania",
// "employerAddress": "Makumbusho",
// "employerPhone": "255700000003",
// "employerEmail": "hr@vodacom.co.tz",
// "jobTitle": "Support Agent",
// "department": "Customer Care",
// "responsibilities": "Call center support",
// "startDate": "2021-05-01",
// "endDate": "2022-05-01",
// "isCurrentlyEmployed": false,
// "employmentType": "FULL_TIME",
// "country": "Tanzania",
// "region": "URBAN_WEST",
// "district": "WETE",
// "city": "Pemba",
// "applicantId": 3
// }
