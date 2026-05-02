package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.PaymentDTO;
import com.znz.tpip_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ================= INITIATE PAYMENT =================
    @PostMapping("/initiate")
    public ResponseEntity<PaymentDTO> initiatePayment(
            @RequestBody PaymentDTO dto) {

        PaymentDTO response = paymentService.initiatePayment(dto);

        return ResponseEntity.ok(response);
    }

    // ================= CONFIRM PAYMENT (WEBHOOK SIMULATION) =================
    @PostMapping("/confirm")
    public ResponseEntity<PaymentDTO> confirmPayment(
            @RequestParam String referenceNumber,
            @RequestParam String transactionId) {

        PaymentDTO response =
                paymentService.confirmPayment(referenceNumber, transactionId);

        return ResponseEntity.ok(response);
    }

    // ================= FAIL PAYMENT =================
    @PostMapping("/fail")
    public ResponseEntity<PaymentDTO> failPayment(
            @RequestParam String referenceNumber) {

        PaymentDTO response =
                paymentService.failPayment(referenceNumber);

        return ResponseEntity.ok(response);
    }

    // ================= CANCEL PAYMENT =================
    @PostMapping("/cancel")
    public ResponseEntity<PaymentDTO> cancelPayment(
            @RequestParam String referenceNumber) {

        PaymentDTO response =
                paymentService.cancelPayment(referenceNumber);

        return ResponseEntity.ok(response);
    }

    // ================= GET PAYMENT STATUS =================
    @GetMapping("/status")
    public ResponseEntity<PaymentDTO> getStatus(
            @RequestParam String referenceNumber) {

        PaymentDTO response =
                paymentService.getPaymentStatus(referenceNumber);

        return ResponseEntity.ok(response);
    }

    // ================= ADMIN: GET ALL PAYMENTS =================
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {

        List<PaymentDTO> response =
                paymentService.getAllPayments();

        return ResponseEntity.ok(response);
    }
}