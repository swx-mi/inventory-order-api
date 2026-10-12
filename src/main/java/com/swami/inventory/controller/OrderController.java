package com.swami.inventory.controller;

import com.swami.inventory.dto.OrderRequest;
import com.swami.inventory.dto.OrderResponse;
import com.swami.inventory.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping
    public Page<OrderResponse> list(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return orderService.list(pageable);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }
}
