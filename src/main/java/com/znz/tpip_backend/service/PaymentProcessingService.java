// package com.znz.tpip_backend.service;

// import com.znz.tpip_backend.enums.ApplicationStep;
// import com.znz.tpip_backend.enums.PaymentStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Payment;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.repository.PaymentRepository;
// import com.znz.tpip_backend.service.security.SignatureService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;

// @Service
// @RequiredArgsConstructor
// public class PaymentProcessingService {

//     private final PaymentRepository paymentRepository;
//     private final SignatureService signatureService;
//     private final ApplicationEventPublisherService eventPublisher;
//     private final ReceiptService receiptService;
//     private final ApplicationRepository applicationRepository;


//     public void process(String provider, String payload, String signature) {

//         if (!signatureService.verify(payload, signature)) {
//             throw new RuntimeException("Invalid signature");
//         }

//         String reference = extractReference(payload);
//         String transactionId = provider + "-" + System.currentTimeMillis();

//         Payment payment = paymentRepository.findByReferenceNumber(reference)
//                 .orElseThrow(() -> new RuntimeException("Payment not found"));

//         if (payment.getStatus() == PaymentStatus.PAID)
//             return;

//         payment.setStatus(PaymentStatus.PAID);
//         payment.setTransactionId(transactionId);
//         payment.setPaymentProviderRef(provider);
//         payment.setPaidAt(LocalDateTime.now());
//         payment.setUpdatedAt(LocalDateTime.now());

//         Payment saved = paymentRepository.save(payment);

//         // ================= IMPORTANT FIX =================
//         Application app = saved.getApplication();

//         // 1. UPDATE APPLICATION STEP IN DATABASE
//         // app.setCurrentStep("SUBMISSION");
//         app.setCurrentStep(ApplicationStep.SUBMISSION);
//         applicationRepository.save(app);

//         // 2. GENERATE RECEIPT
//         receiptService.generateAndSendReceipt(saved);

//         // 3. EVENT (for UI / frontend sync)
//         eventPublisher.publish(
//                 app.getId(),
//                 app.getApplicant().getId(),
//                 ApplicationStep.SUBMISSION);
//     }
 
//     private String extractReference(String payload) {
//         return payload.contains("APP-") ? payload.substring(payload.indexOf("APP-"), payload.indexOf("APP-") + 20)
//                 : "UNKNOWN";
//     }
// }


// package com.znz.tpip_backend.service;

// import com.znz.tpip_backend.enums.ApplicationStep;
// import com.znz.tpip_backend.enums.PaymentStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Payment;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.repository.PaymentRepository;
// import com.znz.tpip_backend.service.security.SignatureService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;

// @Service
// @RequiredArgsConstructor
// public class PaymentProcessingService {

//     private final PaymentRepository paymentRepository;
//     private final SignatureService signatureService;
//     private final ApplicationEventPublisherService eventPublisher;
//     private final ReceiptService receiptService;
//     private final ApplicationRepository applicationRepository;

//     public void process(String provider, String payload, String signature) {

//         // ================= 1. SECURITY CHECK =================
//         if (!signatureService.verify(payload, signature)) {
//             throw new RuntimeException("Invalid signature");
//         }

//         // ================= 2. EXTRACT REFERENCE =================
//         String reference = extractReference(payload);

//         Payment payment = paymentRepository.findByReferenceNumber(reference)
//                 .orElseThrow(() -> new RuntimeException("Payment not found"));

//         // ================= 3. IDENTITY CHECK =================
//         if (payment.getStatus() == PaymentStatus.PAID) {
//             return; // idempotency protection
//         }

//         // ================= 4. UPDATE PAYMENT =================
//         String transactionId = provider + "-" + System.currentTimeMillis();

//         payment.setStatus(PaymentStatus.PAID);
//         payment.setTransactionId(transactionId);
//         payment.setPaymentProviderRef(provider);
//         payment.setPaidAt(LocalDateTime.now());
//         payment.setUpdatedAt(LocalDateTime.now());

//         Payment saved = paymentRepository.save(payment);

//         // ================= 5. APPLICATION UPDATE =================
//         Application app = saved.getApplication();

//         app.setCurrentStep(ApplicationStep.SUBMISSION);
//         applicationRepository.save(app);

//         // ================= 6. RECEIPT =================
//         receiptService.generateAndSendReceipt(saved);

//         // ================= 7. EVENT SYSTEM =================
//         Long applicantId = (app.getApplicant() != null)
//                 ? app.getApplicant().getId()
//                 : null;

//         eventPublisher.publish(
//                 app.getId(),
//                 applicantId,
//                 ApplicationStep.SUBMISSION
//         );
//     }

//     // ================= SAFE REFERENCE EXTRACTION =================
//     private String extractReference(String payload) {

//         // temporary safe fallback (production should parse JSON)
//         if (payload == null) return "UNKNOWN";

//         int index = payload.indexOf("APP-");

//         if (index == -1) return "UNKNOWN";

//         return payload.substring(index, Math.min(index + 25, payload.length()));
//     }
// }
package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PaymentWebhookDTO;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.PaymentRepository;
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

        // ================= 4. UPDATE PAYMENT =================
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaymentProviderRef(dto.getProvider());
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // ================= 5. UPDATE APPLICATION STEP =================
        Application app = saved.getApplication();

        app.setCurrentStep(ApplicationStep.SUBMISSION);
        applicationRepository.save(app);

        // ================= 6. RECEIPT =================
        receiptService.generateAndSendReceipt(saved);

        // ================= 7. EVENT =================
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