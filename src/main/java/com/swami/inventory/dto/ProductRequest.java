package com.swami.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "sku is required") String sku,
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "price is required")
        @DecimalMin(value = "0.01", message = "price must be at least 0.01") BigDecimal price,
        @Min(value = 0, message = "stockQuantity cannot be negative") int stockQuantity) {
}
