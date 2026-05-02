package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PaymentDTO;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.PaymentRepository;
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
    private final ApplicationEventPublisherService eventPublisher;
    private final ModelMapper modelMapper;

    // ================= INITIATE PAYMENT =================
    public PaymentDTO initiatePayment(PaymentDTO dto) {

        Application app = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // 🚨 IMPORTANT: prevent duplicate payment for same application (1:1 rule)
        paymentRepository.findByApplicationId(app.getId())
                .ifPresent(p -> {
                    throw new IllegalStateException("Payment already exists for this application");
                });

        Payment payment = new Payment();

        payment.setApplication(app);
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "TZS");

        payment.setChannel(dto.getChannel());
        payment.setMethod(dto.getMethod());

        payment.setStatus(PaymentStatus.PENDING);
        payment.setReferenceNumber(app.getIndexNumber());
        payment.setInitiatedAt(LocalDateTime.now());

        payment.setPayerPhone(dto.getPayerPhone());
        payment.setPayerName(dto.getPayerName());

        Payment saved = paymentRepository.save(payment);

        // 🔥 EVENT TRIGGER
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep()
        );

        return modelMapper.map(saved, PaymentDTO.class);
    }

    // ================= CONFIRM PAYMENT (WEBHOOK) =================
    public PaymentDTO confirmPayment(String referenceNumber, String transactionId) {

        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // 🚨 prevent double confirmation
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Payment already confirmed");
        }

        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        Application app = saved.getApplication();

        // 🔥 EVENT TRIGGER → moves to SUBMISSION step
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep()
        );

        return modelMapper.map(saved, PaymentDTO.class);
    }

    // ================= FAIL PAYMENT =================
    public PaymentDTO failPayment(String referenceNumber) {

        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());

        return modelMapper.map(paymentRepository.save(payment), PaymentDTO.class);
    }

    // ================= CANCEL PAYMENT =================
    public PaymentDTO cancelPayment(String referenceNumber) {

        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(LocalDateTime.now());

        return modelMapper.map(paymentRepository.save(payment), PaymentDTO.class);
    }

    // ================= GET STATUS =================
    public PaymentDTO getPaymentStatus(String referenceNumber) {

        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return modelMapper.map(payment, PaymentDTO.class);
    }

    // ================= ADMIN: ALL PAYMENTS =================
    public List<PaymentDTO> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(p -> modelMapper.map(p, PaymentDTO.class))
                .toList();
    }
}