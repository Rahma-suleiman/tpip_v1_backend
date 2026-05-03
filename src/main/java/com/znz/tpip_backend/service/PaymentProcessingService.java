
// package com.znz.tpip_backend.service;

// import com.znz.tpip_backend.dto.PaymentWebhookDTO;
// import com.znz.tpip_backend.enums.ApplicationStep;
// import com.znz.tpip_backend.enums.PaymentStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Payment;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.repository.PaymentRepository;
// import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationEventPublisherService;
// import com.znz.tpip_backend.service.security.SignatureService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;

// @Service
// @RequiredArgsConstructor
// public class PaymentProcessingService {

//     private final PaymentRepository paymentRepository;
//     private final ApplicationRepository applicationRepository;
//     private final SignatureService signatureService;
//     private final ApplicationEventPublisherService eventPublisher;
//     private final ReceiptService receiptService;

//     public void process(PaymentWebhookDTO dto) {

//         // ================= 1. SECURITY =================
//         if (!signatureService.verify(dto.toString(), dto.getSignature())) {
//             throw new RuntimeException("Invalid signature");
//         }

//         // ================= 2. FIND PAYMENT =================
//         Payment payment = paymentRepository.findByReferenceNumber(dto.getReferenceNumber())
//                 .orElseThrow(() -> new RuntimeException("Payment not found"));

//         // ================= 3. IDEMPOTENCY =================
//         if (payment.getStatus() == PaymentStatus.PAID) {
//             return;
//         }

//         // ================= 4. UPDATE PAYMENT =================
//         payment.setStatus(PaymentStatus.PAID);
//         payment.setTransactionId(dto.getTransactionId());
//         payment.setPaymentProviderRef(dto.getProvider());
//         payment.setPaidAt(LocalDateTime.now());
//         payment.setUpdatedAt(LocalDateTime.now());

//         Payment saved = paymentRepository.save(payment);

//         // ================= 5. UPDATE APPLICATION STEP =================
//         Application app = saved.getApplication();

//         app.setCurrentStep(ApplicationStep.SUBMISSION);
//         applicationRepository.save(app);

//         // ================= 6. RECEIPT =================
//         receiptService.generateAndSendReceipt(saved);

//         // ================= 7. EVENT =================
//         Long applicantId = (app.getApplicant() != null)
//                 ? app.getApplicant().getId()
//                 : null;

//         eventPublisher.publish(
//                 app.getId(),
//                 applicantId,
//                 ApplicationStep.SUBMISSION
//         );
//     }
// }
package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PaymentWebhookDTO;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.PaymentChannel;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.PaymentRepository;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationEventPublisherService;
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

    public void process(PaymentWebhookDTO dto) {

        // ================= 1. SECURITY =================
        if (!signatureService.verify(dto.toString(), dto.getSignature())) {
            throw new RuntimeException("Invalid signature");
        }

        // ================= 2. FIND PAYMENT =================
        Payment payment = paymentRepository.findByReferenceNumber(dto.getReferenceNumber())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // ================= 3. IDEMPOTENCY =================
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        // ================= 4. PROVIDER VALIDATION (FIXED) =================
        if (payment.getChannel() == null || dto.getProvider() == null) {
            throw new RuntimeException("Provider missing");
        }

        PaymentChannel expectedChannel = payment.getChannel();
        PaymentChannel actualChannel;

        try {
            actualChannel = PaymentChannel.valueOf(dto.getProvider());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid provider: " + dto.getProvider());
        }

        if (expectedChannel != actualChannel) {
            throw new RuntimeException(
                    "Provider mismatch! Expected: " + expectedChannel +
                    " but got: " + actualChannel
            );
        }

        // ================= 5. AMOUNT VALIDATION =================
        if (!payment.getAmount().equals(dto.getAmount())) {
            throw new RuntimeException("Amount mismatch");
        }

        // ================= 6. UPDATE PAYMENT =================
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaymentProviderRef(dto.getProvider());
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // ================= 7. UPDATE APPLICATION =================
        Application app = saved.getApplication();

        app.setCurrentStep(ApplicationStep.SUBMISSION);
        applicationRepository.save(app);

        // ================= 8. RECEIPT =================
        receiptService.generateAndSendReceipt(saved);

        // ================= 9. EVENT =================
        Long applicantId = (app.getApplicant() != null)
                ? app.getApplicant().getId()
                : null;

        eventPublisher.publish(
                app.getId(),
                applicantId,
                ApplicationStep.SUBMISSION
        );
    }
}
// our SignatureService accepts anything containing: "VALID"
// X-Signature: VALID_SIGNATURE/X-Signature: VALID
// MPESA endpoint
// {
//   "referenceNumber": "APP-2026-1-542BFFD3",
//   "transactionId": "MPESA-99887766",
//   "amount": 50000,
//   "phone": "255712345678",
//   "status": "SUCCESS",
//   "provider": "MPESA",
// }
// X-Signature: VALID-TIGO-SIGNATURE
// TIGO endpoint
// {
//   "referenceNumber": "APP-2026-2-9C64AAD6",
//   "transactionId": "TIGO-874512399",
//   "amount": 75000,
//   "phone": "255756789123",
//   "status": "SUCCESS",
//   "provider": "TIGO_PESA"
// }
// {
//   "referenceNumber": "APP-2026-3-7EDAC127",
//   "transactionId": "MPESA-839201",
//   "amount": 0,
//   "signature": "VALID-MPESA-SIGNATURE",
//   "phone": "255789456123",
//   "status": "SUCCESS",
//   "provider": "MPESA"
// }


// USE 2 BELOW TO CHECK IF PAYMENT BY TIGO WILL NOT B SUCCESSFULLY PROCESSED BY MPESA WEBHOOK(OR VICE VERSER)
//  X-Signature:  VALID-MPESA-SIGNATURE (MPESA webhook endpoint)
// {
//   "referenceNumber": "APP-2026-4-7EF0E6BE",
//   "transactionId": "MPESA-74219833",
//   "amount": 50000,
//   "phone": "0789123456",
//   "status": "SUCCESS",
//   "provider": "MPESA"
// }
//  X-Signature:  VALID-TIGO-SIGNATURE (TIGO_PESA webhook endpoint)
// {
//   "referenceNumber": "APP-2026-4-7EF0E6BE",
//   "transactionId": "TIGO-88991234",
//   "amount": 50000,
//   "phone": "0789123456",
//   "status": "SUCCESS",
//   "provider": "TIGO_PESA"
// }