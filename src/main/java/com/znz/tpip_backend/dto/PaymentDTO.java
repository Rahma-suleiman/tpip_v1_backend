// package com.znz.tpip_backend.dto;

// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.znz.tpip_backend.enums.PaymentChannel;
// import com.znz.tpip_backend.enums.PaymentMethod;
// import com.znz.tpip_backend.enums.PaymentStatus;
// import lombok.Data;

// import java.time.LocalDateTime;

// @Data
// public class PaymentDTO {

//     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//     private Long id;

//     private Double amount;
//     private String currency;

//     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//     private String referenceNumber;

//     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//     private String transactionId;

//     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//     private LocalDateTime initiatedAt;

//     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//     private LocalDateTime paidAt;

//     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//     private LocalDateTime updatedAt;

//     private PaymentChannel channel;

//     private PaymentMethod method;

//     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//     private PaymentStatus status;

//     private Long applicationId;

//     private String payerPhone;
//     private String payerName;
// }
package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.PaymentChannel;
import com.znz.tpip_backend.enums.PaymentMethod;
import com.znz.tpip_backend.enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    // ================= INPUT =================
    private Double amount;
    private String currency;

    private PaymentChannel channel;
    private PaymentMethod method;

    private Long applicationId;

    private String payerPhone;
    private String payerName;

    private Boolean feeWaived;

    // ================= SYSTEM GENERATED =================
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String referenceNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String transactionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String paymentProviderRef;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime initiatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime paidAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PaymentStatus status;
}