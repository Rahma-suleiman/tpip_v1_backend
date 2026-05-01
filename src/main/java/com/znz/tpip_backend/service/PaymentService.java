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

        Payment payment = modelMapper.map(dto, Payment.class);

        payment.setApplication(app);
        payment.setCurrency("TZS");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setReferenceNumber(app.getIndexNumber());
        payment.setInitiatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // EVENT TRIGGER
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep());

        return modelMapper.map(saved, PaymentDTO.class);
    }

    // ================= CONFIRM PAYMENT (WEBHOOK) =================
    public PaymentDTO confirmPayment(String referenceNumber, String transactionId) {

        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        Application app = saved.getApplication();

        // EVENT TRIGGER
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep());

        return modelMapper.map(saved, PaymentDTO.class);
    }

    public PaymentDTO getPaymentStatus(String referenceNumber) {

        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public List<PaymentDTO> getAllPayments() {

        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .toList();
    }
}

// {
//   "amount": 50000,
//   "currency": "TZS",
//   "channel": "MPESA",
//   "method": "MOBILE_MONEY",
//   "applicationId": 1,
//   "payerPhone": "255712345678",
//   "payerName": "Rahma Suleiman"
// }
// {
//   "amount": 75000,
//   "currency": "TZS",
//   "channel": "TIGO_PESA",
//   "method": "MOBILE_MONEY",
//   "applicationId": 2,
//   "payerPhone": "255713987654",
//   "payerName": "Ali Hassan"
// }
// {
//   "amount": 100000,
//   "currency": "TZS",
//   "channel": "BANK_TRANSFER",
//   "method": "BANK_TRANSFER",
//   "applicationId": 3,
//   "payerPhone": "255715112233",
//   "payerName": "Neema Joseph"
// }


// Step 7: Admin Review + Decision System (ACCEPT / REJECT / INTERVIEW)
// Or 
// Payment Gateway Integration (M-Pesa simulation)
// Auto receipt PDF generator 

// “Step 8 Interview system”
// or
// “Build scoring engine for applicants”