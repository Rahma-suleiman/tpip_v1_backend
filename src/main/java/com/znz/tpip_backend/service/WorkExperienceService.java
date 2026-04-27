package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.WorkExperienceDto;
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
    private final ModelMapper modelMapper;

    // ================= CREATE =================
    public WorkExperienceDto create(WorkExperienceDto dto) {

        Applicant applicant = applicantRepository.findById(dto.getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        // ================= VALIDATION =================
        validate(dto);

        // ================= ENTITY MAP =================
        WorkExperience work = modelMapper.map(dto, WorkExperience.class);

        work.setApplicant(applicant);

        WorkExperience saved = workExperienceRepository.save(work);

        return mapToDto(saved);
    }

    // ================= UPDATE =================
    public WorkExperienceDto update(Long id, WorkExperienceDto dto) {

        WorkExperience work = workExperienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work experience not found"));

        validate(dto);

        modelMapper.map(dto, work);

        WorkExperience updated = workExperienceRepository.save(work);

        return mapToDto(updated);
    }

    // ================= GET BY APPLICANT =================
    public List<WorkExperienceDto> getByApplicant(Long applicantId) {

        return workExperienceRepository.findByApplicantId(applicantId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ================= DELETE =================
    public void delete(Long id) {
        workExperienceRepository.deleteById(id);
    }

    // ================= VALIDATION RULES =================
    private void validate(WorkExperienceDto dto) {

        // OPTION: no experience flag logic
        if (dto.getEmployerName() == null || dto.getEmployerName().isBlank()) {
            throw new RuntimeException("Employer name is required");
        }

        if (dto.getJobTitle() == null || dto.getJobTitle().isBlank()) {
            throw new RuntimeException("Job title is required");
        }

        if (dto.getStartDate() == null) {
            throw new RuntimeException("Start date is required");
        }

        // If not currently employed → end date required
        if (Boolean.FALSE.equals(dto.getIsCurrentlyEmployed())) {
            if (dto.getEndDate() == null) {
                throw new RuntimeException("End date is required if not currently employed");
            }
        }

        // Date logic check
        if (dto.getEndDate() != null && dto.getStartDate() != null) {
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new RuntimeException("End date cannot be before start date");
            }
        }
    }

    // ================= MAPPING =================
    private WorkExperienceDto mapToDto(WorkExperience work) {

        WorkExperienceDto dto = modelMapper.map(work, WorkExperienceDto.class);

        // fk mapping
        dto.setApplicantId(work.getApplicant().getId());

        // reserve
        if (work.getDocuments() != null) {
            dto.setDocumentIds(
                    work.getDocuments()
                            .stream()
                            .map(Document::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}