package com.znz.tpip_backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Data;

@Data
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    private String name;
    private String email;
    private String password;
    
    // reverse
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> applicationIds;

}
