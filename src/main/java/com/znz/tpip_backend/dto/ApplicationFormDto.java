package com.znz.tpip_backend.dto;

import java.util.List;
import lombok.Data;

@Data
public class ApplicationFormDto {

    // ================= STEP 1: PERSONAL INFO =================
    private PersonalInfoDto personalInfo;

    // ================= STEP 2: EDUCATION =================
    private List<EducationDto> educations;

    // ================= STEP 3: WORK =================
    private Boolean hasWorkExperience;
    private List<WorkExperienceDto> workExperiences;

    // ================= STEP 4: PROGRAMMES (LINKING EXISTING DATA) =================
    private List<Long> programmeChoiceIds;

    // ================= STEP 5: REFEREES =================
    private List<RefereeDTO> referees;

    // ================= STEP 6: PAYMENT =================
    private PaymentDTO payment;
}