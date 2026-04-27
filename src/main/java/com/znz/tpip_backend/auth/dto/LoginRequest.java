package com.znz.tpip_backend.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
// User sends:
// - email
// - password

// ↓
// System:
// ✔ find user
// ✔ verify password
// ✔ check account status

// ↓
// Return success message