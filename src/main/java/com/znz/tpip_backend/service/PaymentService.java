package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.PaymentDTO;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.*;
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


    // public PaymentDTO confirmPayment(String referenceNumber, String transactionId) {

    //     Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
    //             .orElseThrow(() -> new RuntimeException("Payment not found"));

    //     if (payment.getStatus() == PaymentStatus.PAID) {
    //         throw new RuntimeException("Payment already confirmed");
    //     }

    //     payment.setTransactionId(transactionId);
    //     payment.setStatus(PaymentStatus.PAID);
    //     payment.setPaidAt(LocalDateTime.now());
    //     payment.setUpdatedAt(LocalDateTime.now());

    //     Payment saved = paymentRepository.save(payment);

    //     Application app = saved.getApplication();

    //     eventPublisher.publish(
    //             app.getId(),
    //             app.getApplicant().getId(),
    //             app.getCurrentStep());

    //     return modelMapper.map(saved, PaymentDTO.class);
    // }

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
}
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
// Real MPESA Daraja API integration
// ✔ 
// Real Tigo Pesa API flow
// ✔ 
// PDF receipt generator (download endpoint)
// ✔ 
// Finance dashboard (reconciliation system)
// ✔ 
// Admin fee waiver approval workflow