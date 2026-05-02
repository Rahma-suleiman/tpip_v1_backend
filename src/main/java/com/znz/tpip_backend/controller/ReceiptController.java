package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.service.PaymentService;
import com.znz.tpip_backend.service.ReceiptPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final PaymentService paymentService;
    private final ReceiptPdfService receiptPdfService;

    @GetMapping("/download/{referenceNumber}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String referenceNumber) {

        Payment payment = paymentService.getPaymentEntity(referenceNumber);

        byte[] pdf = receiptPdfService.generateReceiptPdf(payment);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=TPIP-RECEIPT-" + referenceNumber + ".pdf")
                .body(pdf);
    }
}