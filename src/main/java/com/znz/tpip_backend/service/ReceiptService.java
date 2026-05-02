package com.znz.tpip_backend.service;

import com.znz.tpip_backend.email.EmailService;
import com.znz.tpip_backend.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final EmailService emailService;

    public void generateAndSendReceipt(Payment payment) {

        String receipt = """
                PAYMENT RECEIPT
                ----------------------
                Reference: %s
                Amount: %.2f %s
                Status: PAID
                Transaction: %s
                """.formatted(
                payment.getReferenceNumber(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getTransactionId()
        );

        emailService.sendEmail(
                payment.getPayerPhone(),
                "TPIP Payment Receipt",
                receipt
        );
    }
}