package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.District;
import com.znz.tpip_backend.enums.EmploymentType;
import com.znz.tpip_backend.enums.Region;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WorkExperienceDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String employerName;
    private String employerAddress;
    private String employerPhone;

    private String employerEmail;

   
    private String jobTitle;
    private String department;
    // private String jobDescription;
    private String responsibilities;

    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrentlyEmployed;

    private EmploymentType employmentType;

    private String country;
    private Region region;
    private District district;
    private String city;

    // FK
    private Long applicantId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> documentIds;
}