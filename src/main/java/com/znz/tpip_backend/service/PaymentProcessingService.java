package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PaymentWebhookDTO;
import com.znz.tpip_backend.enums.ApplicationStep;
// import com.znz.tpip_backend.enums.PaymentChannel;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.PaymentRepository;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationEventPublisherService;
// import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationStepGuard;
import com.znz.tpip_backend.service.security.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentRepository paymentRepository;
    private final ApplicationRepository applicationRepository;
    private final SignatureService signatureService;
    private final ApplicationEventPublisherService eventPublisher;
    private final ReceiptService receiptService;
    // private final ApplicationStepGuard stepGuard;

    public void process(PaymentWebhookDTO dto) {

        if (!signatureService.verify(dto.toString(), dto.getSignature())) {
            throw new RuntimeException("Invalid signature");
        }

        Payment payment = paymentRepository.findByReferenceNumber(dto.getReferenceNumber())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        Application app = payment.getApplication();

        // ✅ STRICT CHECK (NOT stepGuard)
        if (app.getCurrentStep() != ApplicationStep.PAYMENT) {
            throw new RuntimeException("Application not in PAYMENT step");
        }

        if (!payment.getAmount().equals(dto.getAmount())) {
            throw new RuntimeException("Amount mismatch");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaymentProviderRef(dto.getProvider());
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        app.setCurrentStep(ApplicationStep.SUBMISSION);
        applicationRepository.save(app);

        receiptService.generateAndSendReceipt(saved);

        eventPublisher.publish(app.getId(), app.getApplicant().getId(), ApplicationStep.SUBMISSION);
    }
}
// our SignatureService accepts anything containing: "VALID"
// X-Signature: VALID_SIGNATURE/X-Signature: VALID
// MPESA endpoint
// {
// "referenceNumber": "APP-2026-1-542BFFD3",
// "transactionId": "MPESA-99887766",
// "amount": 50000,
// "phone": "255712345678",
// "status": "SUCCESS",
// "provider": "MPESA",
// }
// X-Signature: VALID-TIGO-SIGNATURE
// TIGO endpoint
// {
// "referenceNumber": "APP-2026-2-9C64AAD6",
// "transactionId": "TIGO-874512399",
// "amount": 75000,
// "phone": "255756789123",
// "status": "SUCCESS",
// "provider": "TIGO_PESA"
// }
// {
// "referenceNumber": "APP-2026-3-7EDAC127",
// "transactionId": "MPESA-839201",
// "amount": 0,
// "signature": "VALID-MPESA-SIGNATURE",
// "phone": "255789456123",
// "status": "SUCCESS",
// "provider": "MPESA"
// }

// USE 2 BELOW TO CHECK IF PAYMENT BY TIGO WILL NOT B SUCCESSFULLY PROCESSED BY
// MPESA WEBHOOK(OR VICE VERSER)
// X-Signature: VALID-MPESA-SIGNATURE (MPESA webhook endpoint)
// {
// "referenceNumber": "APP-2026-4-7EF0E6BE",
// "transactionId": "MPESA-74219833",
// "amount": 50000,
// "phone": "0789123456",
// "status": "SUCCESS",
// "provider": "MPESA"
// }
// X-Signature: VALID-TIGO-SIGNATURE (TIGO_PESA webhook endpoint)
// {
// "referenceNumber": "APP-2026-4-7EF0E6BE",
// "transactionId": "TIGO-88991234",
// "amount": 50000,
// "phone": "0789123456",
// "status": "SUCCESS",
// "provider": "TIGO_PESA"
// }