package com.quickcommerce.order_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quickcommerce.order_service.entity.Order;
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
@Schema(description = "Order Information")
public class OrderDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("orderNumber")
    @Schema(example = "ORD-2024-001")
    private String orderNumber;

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("totalAmount")
    @Schema(example = "1999.98")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    @Schema(example = "PENDING")
    private String status;

    @JsonProperty("shippingAddress")
    private String shippingAddress;

    @JsonProperty("billingAddress")
    private String billingAddress;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}