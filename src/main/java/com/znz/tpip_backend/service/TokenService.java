package com.znz.tpip_backend.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public LocalDateTime expiryTime() {
        return LocalDateTime.now().plusDays(3);
    }
}