package com.znz.tpip_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FeedbackDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    private int rating;
    
    private String comment;
    
    private LocalDate date;
    
    private String sessionTopic;      
    private String performanceLevel;  
    private String recommendations;  

    // forward r/ship(inputs)
    private Long mentorId;

    private Long internId; 
    
    private Long placementId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mentorName;
        
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String internName;

    
}
