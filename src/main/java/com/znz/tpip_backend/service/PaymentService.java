package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PaymentDTO;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.*;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationEventPublisherService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationRepository applicationRepository;
    private final PaymentReferenceService referenceService;
    private final ApplicationEventPublisherService eventPublisher;
    private final ModelMapper modelMapper;

    public PaymentDTO initiatePayment(PaymentDTO dto) {

        Application app = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        paymentRepository.findByApplicationId(app.getId())
                .ifPresent(p -> {
                    throw new RuntimeException("Payment already exists");
                });

        Payment payment = new Payment();

        payment.setApplication(app);
        payment.setAmount(dto.getAmount());
        payment.setCurrency("TZS");

        payment.setChannel(dto.getChannel());
        payment.setMethod(dto.getMethod());

        payment.setPayerPhone(dto.getPayerPhone());
        payment.setPayerName(dto.getPayerName());

        payment.setReferenceNumber(referenceService.generateReference(app.getId()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setInitiatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        return modelMapper.map(saved, PaymentDTO.class);
    }

    public PaymentDTO getPaymentStatus(String ref) {
        return modelMapper.map(
                paymentRepository.findByReferenceNumber(ref)
                        .orElseThrow(),
                PaymentDTO.class);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(p -> modelMapper.map(p, PaymentDTO.class))
                .toList();
    }
    public Payment getPaymentEntity(String referenceNumber) {
    return paymentRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
}
}
// REAL PAYMENT FLOW (IMPORTANT)

// Correct architecture:

// 1. initiatePayment()
//    → creates PENDING payment
//    → sends user to pay (MPESA/TIGO)

// 2. WEBHOOK (confirmPayment / PaymentProcessingService)
//    → TRUSTED source
//    → marks PAID
//    → triggers application step → SUBMISSION
// {
// "amount": 50000,
// "currency": "TZS",
// "channel": "MPESA",
// "method": "MOBILE_MONEY",
// "applicationId": 1,
// "payerPhone": "255712345678",
// "payerName": "Amina Hassan",
// "feeWaived": false
// }
// {
//   "amount": 75000,
//   "currency": "TZS",
//   "channel": "TIGO_PESA",
//   "method": "MOBILE_MONEY",
//   "applicationId": 2,
//   "payerPhone": "255756789123",
//   "payerName": "Mohamed Juma",
//   "feeWaived": false
// }
// {
//   "amount": 0,
//   "currency": "TZS",
//   "channel": "MPESA",
//   "method": "MOBILE_MONEY",
//   "applicationId": 3,
//   "payerPhone": "255789456123",
//   "payerName": "Fatma Said",
//   "feeWaived": true
// }
// {
//   "amount": 0,
//   "currency": "TZS",
//   "channel": "BANK_TRANSFER",
//   "method": "BANK_TRANSFER",
//   "applicationId": 3,
//   "payerPhone": "255789456123",
//   "payerName": "Fatma Said",
//   "feeWaived": true
// }
// {
//   "amount": 50000,
//   "currency": "TZS",
//   "channel": "MPESA",
//   "method": "MOBILE_MONEY",
//   "applicationId": 4,
//   "payerPhone": "0789123456",
//   "payerName": "Rahma Suleiman",
//   "feeWaived": false
// }
// Real MPESA Daraja API integration
// ✔ 
// Real Tigo Pesa API flow
// ✔ 
// PDF receipt generator (download endpoint)
// ✔ 
// Finance dashboard (reconciliation system)
// ✔ 
// Admin fee waiver approval workflow



// Admin fee waiver approval workflow
// Applicant requests waiver
//         ↓
// Application marked: WAIVER_PENDING
//         ↓
// Admin reviews request
//         ↓
// APPROVE → payment auto-bypassed (Step advances to SUBMISSION)
// REJECT  → applicant must pay normally
//         ↓
// Audit log + event-driven step update

// now lets proceed with this"Step 7 Admin Review system (ACCEPT/REJECT/INTERVIEW)"