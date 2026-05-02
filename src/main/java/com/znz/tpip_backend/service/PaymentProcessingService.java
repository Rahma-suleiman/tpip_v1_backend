package com.znz.tpip_backend.service;

import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.PaymentRepository;
import com.znz.tpip_backend.service.security.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentRepository paymentRepository;
    private final SignatureService signatureService;
    private final ApplicationEventPublisherService eventPublisher;
    private final ReceiptService receiptService;

    public void process(String provider, String payload, String signature) {

        // 1. SECURITY CHECK
        if (!signatureService.verify(payload, signature)) {
            throw new RuntimeException("Invalid signature");
        }

        // 2. EXTRACT DATA (SIMPLIFIED)
        String reference = extractReference(payload);
        String transactionId = provider + "-" + System.currentTimeMillis();

        Payment payment = paymentRepository.findByReferenceNumber(reference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID) return;

        // 3. UPDATE PAYMENT
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(transactionId);
        payment.setPaymentProviderRef(provider);
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // 4. GENERATE RECEIPT
        receiptService.generateAndSendReceipt(saved);

        // 5. MOVE APPLICATION TO NEXT STEP
        eventPublisher.publish(
                saved.getApplication().getId(),
                saved.getApplication().getApplicant().getId(),
                saved.getApplication().getCurrentStep()
        );
    }

    private String extractReference(String payload) {
        return payload.contains("APP-") ? payload.substring(payload.indexOf("APP-"), payload.indexOf("APP-") + 20)
                : "UNKNOWN";
    }
}