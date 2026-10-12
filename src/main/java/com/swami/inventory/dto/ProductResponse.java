package com.swami.inventory.dto;

import com.swami.inventory.entity.Product;
import java.math.BigDecimal;

public record ProductResponse(Long id, String sku, String name, BigDecimal price, int stockQuantity) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getPrice(), p.getStockQuantity());
    }
}
