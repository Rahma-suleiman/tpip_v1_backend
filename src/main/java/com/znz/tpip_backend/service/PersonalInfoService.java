package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PersonalInfoDto;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.ApplicantRepository;
import com.znz.tpip_backend.repository.PersonalInfoRepository;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalInfoService {

    private final PersonalInfoRepository personalInfoRepository;
    private final ApplicantRepository applicantRepository;
    private final ModelMapper modelMapper;

    // ================= CREATE / UPDATE =================
    public PersonalInfoDto save(Long applicantId, PersonalInfoDto dto) {

        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        PersonalInfo info = personalInfoRepository
                .findByApplicantId(applicantId)
                .orElse(new PersonalInfo());

        // ================= SAFE AUTO MAPPING =================
        modelMapper.map(dto, info);

        info.setApplicant(applicant);

        // Next of Kin
        if (dto.getNextOfKinName() != null) {
            NextOfKin nok = info.getNextOfKin() != null
                    ? info.getNextOfKin()
                    : new NextOfKin();

            nok.setKinFullName(dto.getNextOfKinName());
            nok.setKinRelationship(dto.getNextOfKinRelationship());
            nok.setKinPhoneNumber(dto.getNextOfKinPhone());

            info.setNextOfKin(nok);
        }

        // Disability
        if (dto.getHasDisability() != null) {
            Disability disability = info.getDisability() != null
                    ? info.getDisability()
                    : new Disability();

            disability.setHasDisability(dto.getHasDisability());
            disability.setDisabilityType(dto.getDisabilityType());
            disability.setDisabilityNeeds(dto.getDisabilityNeeds());

            info.setDisability(disability);
        }

        PersonalInfo saved = personalInfoRepository.save(info);

        return mapToDto(saved);
    }

    // ================= GET =================
    public PersonalInfoDto getByApplicant(Long applicantId) {

        PersonalInfo info = personalInfoRepository.findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Personal info not found"));

        return mapToDto(info);
    }

    // ================= DELETE =================
    public void delete(Long id) {
        personalInfoRepository.deleteById(id);
    }

    // ================= MAPPER =================
    private PersonalInfoDto mapToDto(PersonalInfo info) {

        PersonalInfoDto dto = modelMapper.map(info, PersonalInfoDto.class);

        // FK
        dto.setApplicantId(info.getApplicant().getId());

        // reverse
        if (info.getDocuments() != null) {
            dto.setDocumentIds(
                    info.getDocuments()
                            .stream()
                            .map(Document::getId)
                            .collect(Collectors.toList()));
        }

        return dto;
    }
}