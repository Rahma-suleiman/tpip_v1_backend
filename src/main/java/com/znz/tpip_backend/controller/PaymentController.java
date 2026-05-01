package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.PaymentDTO;
import com.znz.tpip_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;

import java.util.List;

// import org.hibernate.mapping.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ================= INITIATE PAYMENT =================
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentDTO dto) {
        try {
            PaymentDTO response = paymentService.initiatePayment(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    // ================= CONFIRM PAYMENT (WEBHOOK) =================
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestParam String referenceNumber,
            @RequestParam String transactionId) {
        try {
            PaymentDTO response = paymentService.confirmPayment(referenceNumber, transactionId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    // ================= CHECK PAYMENT STATUS =================
    @GetMapping("/status/{referenceNumber}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String referenceNumber) {
        try {
            PaymentDTO response = paymentService.getPaymentStatus(referenceNumber);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    // ================= GET ALL PAYMENTS =================
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try {
            List<PaymentDTO> response = paymentService.getAllPayments();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch payments");
        }
    }

    
}