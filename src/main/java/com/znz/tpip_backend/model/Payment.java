// package com.znz.tpip_backend.model;

// import jakarta.persistence.*;
// import lombok.*;
// import java.time.LocalDateTime;

// import com.znz.tpip_backend.enums.PaymentMethod;
// import com.znz.tpip_backend.enums.PaymentStatus;

// @Getter
// @Setter
// @Entity
// @Table(name = "payment")
// public class Payment extends AuditModel<String> {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     // ===== PAYMENT DETAILS =====
//     @Column(nullable = false)
//     private Double amount;

//     @Column(nullable = false)
//     private String currency; // TZS, USD

//     @Enumerated(EnumType.STRING)
//     private PaymentStatus paymentStatus;

//     @Enumerated(EnumType.STRING)
//     private PaymentMethod paymentMethod;

//     // ===== TRANSACTION INFO =====
//     private String transactionReference;

//     private String externalTransactionId;

//     private String receiptNumber;

//     // ===== PAYER DETAILS =====
//     private String payerName;

//     private String payerPhone;

//     private String payerEmail;

//     // ===== PAYMENT DATE =====
//     private LocalDateTime paymentDate;

//     private LocalDateTime confirmedDate;

//     // ===== SYSTEM CONTROL =====
//     private Boolean isVerified = false;

//     private String verifiedBy;

//     private String paymentDescription;

//     // ===== RELATIONSHIP =====
//     @ManyToOne
//     @JoinColumn(name = "personal_info_id", nullable = false)
//     private PersonalInfo personalInfo;
    
// }