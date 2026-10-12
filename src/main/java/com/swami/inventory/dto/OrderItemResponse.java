package com.swami.inventory.dto;

import com.swami.inventory.entity.OrderItem;
import java.math.BigDecimal;

public record OrderItemResponse(Long productId, String productName, int quantity,
                                BigDecimal unitPrice, BigDecimal lineTotal) {

    public static OrderItemResponse from(OrderItem i) {
        return new OrderItemResponse(i.getProduct().getId(), i.getProduct().getName(),
                i.getQuantity(), i.getUnitPrice(), i.getLineTotal());
    }
}
