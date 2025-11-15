package com.quickcommerce.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quickcommerce.payment_service.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment Information")
public class PaymentDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("amount")
    @Schema(example = "999.99")
    private BigDecimal amount;

    @JsonProperty("paymentMethod")
    @Schema(example = "CREDIT_CARD")
    private String paymentMethod;

    @JsonProperty("status")
    @Schema(example = "COMPLETED")
    private String status;

    @JsonProperty("referenceNumber")
    private String referenceNumber;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    public static PaymentDTO from(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().toString())
                .status(payment.getStatus().toString())
                .referenceNumber(payment.getReferenceNumber())
                .errorMessage(payment.getErrorMessage())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}