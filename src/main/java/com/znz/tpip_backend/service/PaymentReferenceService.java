package com.znz.tpip_backend.service;

import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.UUID;

@Service
public class PaymentReferenceService {

    public String generateReference(Long applicationId) {
        return "APP-" + Year.now().getValue() + "-" +
                applicationId + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}