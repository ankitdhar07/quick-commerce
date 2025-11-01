package com.quickcommerce.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quickcommerce.product_service.entity.Product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Valid
@Schema(description = "Product Information")
public class ProductDTO {
    @JsonProperty("id")
    @Schema(description = "Product ID", example = "1")
    private Long id;

    @JsonProperty("sku")
    @Schema(description = "Product SKU", example = "PROD-001")
    private String sku;

    @JsonProperty("name")
    @Schema(description = "Product Name", example = "Laptop")
    private String name;

    @JsonProperty("description")
    @Schema(description = "Product Description")
    private String description;

    @JsonProperty("price")
    @Schema(description = "Product Price", example = "999.99")
    private BigDecimal price;

    @JsonProperty("quantity")
    @Schema(description = "Available Quantity", example = "50")
    private Integer quantity;

    @JsonProperty("category")
    @Schema(description = "Product Category", example = "Electronics")
    private String category;

    @JsonProperty("status")
    @Schema(description = "Product Status", example = "ACTIVE")
    private String status;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    public static ProductDTO from(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .status(product.getStatus().toString())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
