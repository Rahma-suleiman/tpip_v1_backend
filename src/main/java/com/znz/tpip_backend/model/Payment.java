// package com.znz.tpip_backend.model;

// import jakarta.persistence.*;
// import lombok.*;
// import java.time.LocalDateTime;

// import com.znz.tpip_backend.enums.PaymentChannel;
// import com.znz.tpip_backend.enums.PaymentMethod;
// import com.znz.tpip_backend.enums.PaymentStatus;

// @Getter
// @Setter
// @Entity
// @Table(name = "payments")
// public class Payment {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     // ================= PAYMENT DETAILS =================
//     private Double amount;
//     private String currency;

//     private String referenceNumber;   // APP/2026/12345
//     private String transactionId;      // gateway transaction id

//     private LocalDateTime initiatedAt;
//     private LocalDateTime paidAt;
//     private LocalDateTime updatedAt;


//     // ================= PAYMENT METHOD =================
//     @Enumerated(EnumType.STRING)
//     private PaymentMethod method; //Type of payment (Mobile, Bank, Card)

//     @Enumerated(EnumType.STRING)
//     private PaymentChannel channel; //Specific provider (MPESA, TIGO, etc.)

//     @Enumerated(EnumType.STRING)
//     private PaymentStatus status = PaymentStatus.PENDING;

//     // ================= RELATIONSHIP (1:1 RULE) =================
//     @OneToOne
//     @JoinColumn(name = "application_id", nullable = false, unique = true)
//     private Application application;

//     // ================= AUDIT / CONTROL =================
//     private String payerPhone;
//     private String payerName;
// }
package com.znz.tpip_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.znz.tpip_backend.enums.PaymentChannel;
import com.znz.tpip_backend.enums.PaymentMethod;
import com.znz.tpip_backend.enums.PaymentStatus;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= PAYMENT DETAILS =================
    private Double amount;
    private String currency;

    @Column(unique = true, updatable = false)
    private String referenceNumber; // APP/2026/12345 (immutable)

    private String transactionId; // internal tracking
    private String paymentProviderRef; // MPESA / PESA PAL / TIGO ref

    private LocalDateTime initiatedAt;
    private LocalDateTime paidAt;
    private LocalDateTime updatedAt;

    // ================= PAYMENT METHOD =================
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentChannel channel;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    // ================= RELATIONSHIP =================
    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    // ================= AUDIT =================
    private String payerPhone;
    private String payerName;

    // ================= BUSINESS RULE =================
    private Boolean feeWaived = false;
}