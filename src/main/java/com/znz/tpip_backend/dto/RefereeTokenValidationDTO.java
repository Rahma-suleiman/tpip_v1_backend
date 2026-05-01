package com.znz.tpip_backend.dto;

import lombok.Data;

@Data
public class RefereeTokenValidationDTO {

    private Long refereeId;
    private String fullName;
    private String email;
    private String applicationIndex;

    private boolean valid;
    private String message;
}