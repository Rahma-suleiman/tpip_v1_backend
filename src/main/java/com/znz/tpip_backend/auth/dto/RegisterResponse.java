package com.znz.tpip_backend.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private String applicationIndexNumber;
}
