package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentWebhookDTO {

    // ===== REQUIRED FROM GATEWAY =====
    private String referenceNumber;
    private String transactionId;
    private Double amount;

    // ===== SECURITY =====
    private String signature;

    // ===== OPTIONAL METADATA (real gateways send this) =====
    private String phone;
    private String status; // SUCCESS / FAILED
    private String provider; // MPESA / TIGO
}