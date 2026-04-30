package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.DocumentDto;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Document;
import com.znz.tpip_backend.model.Education;
import com.znz.tpip_backend.model.WorkExperience;
import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.model.*;
// import com.znz.tpip_backend.repository.*;
import com.znz.tpip_backend.repository.DocumentRepository;
import com.znz.tpip_backend.repository.EducationRepository;
import com.znz.tpip_backend.repository.WorkExperienceRepository;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;
    private final EducationRepository educationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final ModelMapper modelMapper;

    // ================= CREATE =================
    public DocumentDto create(DocumentDto dto) {

        Application app = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Document doc = new Document();
        doc.setDocumentType(dto.getDocumentType());
        doc.setFileUrl(dto.getFileUrl());
        doc.setIsVerified(false);
        doc.setApplication(app);

        // OPTIONAL LINKS
        if (dto.getEducationId() != null) {
            Education edu = educationRepository.findById(dto.getEducationId())
                    .orElseThrow(() -> new RuntimeException("Education not found"));
            doc.setEducation(edu);
        }

        if (dto.getWorkExperienceId() != null) {
            WorkExperience work = workExperienceRepository.findById(dto.getWorkExperienceId())
                    .orElseThrow(() -> new RuntimeException("Work experience not found"));
            doc.setWorkExperience(work);
        }

        return mapToDto(documentRepository.save(doc));
    }

    // ================= GET BY APPLICATION =================
    public List<DocumentDto> getByApplication(Long applicationId) {

        return documentRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ================= DELETE =================
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    // ================= MAPPER =================
    private DocumentDto mapToDto(Document doc) {

        DocumentDto dto = modelMapper.map(doc, DocumentDto.class);

        dto.setApplicationId(doc.getApplication().getId());

        if (doc.getEducation() != null)
            dto.setEducationId(doc.getEducation().getId());

        if (doc.getWorkExperience() != null)
            dto.setWorkExperienceId(doc.getWorkExperience().getId());

        return dto;
    }
}