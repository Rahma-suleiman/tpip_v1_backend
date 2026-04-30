package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PersonalInfoDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String firstName;

    private String middleName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String nationality;

    private String phoneNumber;

    private String alternativePhone;

    private String email;

    private Region region;

    private District district;

    // private String ward;
    // private String street;

    private String nextOfKinName;
    private RelationshipType nextOfKinRelationship;
    private String nextOfKinPhone;
    
    private Boolean hasDisability;
    private DisabilityType disabilityType;
    private String disabilityNeeds;

   

    // fk
    private Long applicantId;
   
}