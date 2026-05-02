package com.znz.tpip_backend.service.security;

import org.springframework.stereotype.Service;

@Service
public class SignatureService {

    private static final String SECRET = "TPIP_SECRET_KEY";

    public boolean verify(String payload, String signature) {
        // SIMPLE SIMULATION (replace with HMAC-SHA256 in real system)
        return signature != null && signature.contains("VALID");
    }
}