package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PersonalInfoDto;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.ApplicantRepository;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.PersonalInfoRepository;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalInfoService {

    private final PersonalInfoRepository personalInfoRepository;
    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationWorkflowService workflowService;
    private final ModelMapper modelMapper;

    // ================= CREATE / UPDATE =================
   public PersonalInfoDto save(Long applicantId, PersonalInfoDto dto) {

    Applicant applicant = applicantRepository.findById(applicantId)
            .orElseThrow(() -> new RuntimeException("Applicant not found"));

    PersonalInfo info = personalInfoRepository
            .findByApplicantId(applicantId)
            .orElse(new PersonalInfo());

    // ================= MAP =================
    modelMapper.map(dto, info);
    info.setApplicant(applicant);

    // ================= NEXT OF KIN =================
    if (dto.getNextOfKinName() != null) {

        NextOfKin nok = info.getNextOfKin() != null
                ? info.getNextOfKin()
                : new NextOfKin();

        nok.setKinFullName(dto.getNextOfKinName());
        nok.setKinRelationship(dto.getNextOfKinRelationship());
        nok.setKinPhoneNumber(dto.getNextOfKinPhone());

        info.setNextOfKin(nok);
    }

    // ================= DISABILITY =================
    if (dto.getHasDisability() != null) {

        Disability disability = info.getDisability() != null
                ? info.getDisability()
                : new Disability();

        disability.setHasDisability(dto.getHasDisability());
        disability.setDisabilityType(dto.getDisabilityType());
        disability.setDisabilityNeeds(dto.getDisabilityNeeds());

        info.setDisability(disability);
    }

    // ================= SAVE PERSONAL INFO =================
    PersonalInfo saved = personalInfoRepository.save(info);

    // ================= AUTO STEP TRACKING =================
    Application app = getApplication(applicantId);

    workflowService.evaluateAndAdvance(app.getId());

    return mapToDto(saved);
}
    private Application getApplication(Long applicantId) {
        return applicationRepository
                .findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
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

        return dto;
    }
}

// {
// "firstName": "Amina",
// "middleName": "Salum",
// "lastName": "Hassan",
// "dateOfBirth": "2000-05-12",
// "gender": "FEMALE",
// "nationality": "Tanzanian",
// "phoneNumber": "0712345678",
// "alternativePhone": "0788888888",
// "email": "amina@gmail.com",
// "region": "URBAN_WEST",
// "district": "MJINI",
// "nextOfKinName": "Salum Hassan",
// "nextOfKinRelationship": "FATHER",
// "nextOfKinPhone": "0711111111",
// "hasDisability": false,
// "disabilityType": "NONE",
// "disabilityNeeds": "",
// "applicantId": 1
// }
// {
// "firstName": "Mohamed",
// "middleName": "Ali",
// "lastName": "Juma",
// "dateOfBirth": "1999-08-21",
// "gender": "MALE",
// "nationality": "Tanzanian",
// "phoneNumber": "0756789123",
// "alternativePhone": "0750000000",
// "email": "mohamed@gmail.com",
// "region": "URBAN_WEST",
// "district": "KUSINI",
// "nextOfKinName": "Ali Juma",
// "nextOfKinRelationship": "FATHER",
// "nextOfKinPhone": "0751111111",
// "hasDisability": false,
// "disabilityType": "NONE",
// "disabilityNeeds": "",
// "applicantId": 2
// }
// {
// "firstName": "Fatma",
// "middleName": "Omar",
// "lastName": "Said",
// "dateOfBirth": "2001-01-10",
// "gender": "FEMALE",
// "nationality": "Tanzanian",
// "phoneNumber": "0789456123",
// "alternativePhone": "0780000000",
// "email": "fatma@gmail.com",
// "region": "URBAN_WEST",
// "district": "WETE",
// "nextOfKinName": "Omar Said",
// "nextOfKinRelationship": "FATHER",
// "nextOfKinPhone": "0781111111",
// "hasDisability": false,
// "disabilityType": "NONE",
// "disabilityNeeds": "",
// "applicantId": 3
// }
