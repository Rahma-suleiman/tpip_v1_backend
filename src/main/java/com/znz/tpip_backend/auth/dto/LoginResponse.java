package com.znz.tpip_backend.auth.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private Long userId;
    private Long applicantId;
    private String email;
    private String role;

    private String applicationIndexNumber;
    private String firstName;

    private String middleName;

    private String lastName;
}