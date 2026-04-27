package com.znz.tpip_backend.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.District;
import com.znz.tpip_backend.enums.Region;
import com.znz.tpip_backend.enums.SchoolType;

import lombok.Data;

@Data
public class SchoolDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;

    private String location;

    private Region region;

    private District district;

    private Set<SchoolType> schoolTypes;

    private int capacity;  //max interns allowed
    
    private int currentInternCount; //current number of interns placed in the school/how many assigned

    private String phoneNumber;

    private String email;

    private String headTeacherName;

    // reverse r/ship
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> placementIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> mentorIds;
}
