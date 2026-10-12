package com.swami.inventory.dto;

import com.swami.inventory.entity.CustomerOrder;
import com.swami.inventory.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, Long customerId, String customerName, LocalDateTime orderDate,
                            OrderStatus status, BigDecimal totalAmount, List<OrderItemResponse> items) {

    public static OrderResponse from(CustomerOrder o) {
        return new OrderResponse(
                o.getId(),
                o.getCustomer().getId(),
                o.getCustomer().getName(),
                o.getOrderDate(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
