package com.swami.inventory.service;

import com.swami.inventory.dto.OrderItemRequest;
import com.swami.inventory.dto.OrderRequest;
import com.swami.inventory.dto.OrderResponse;
import com.swami.inventory.entity.Customer;
import com.swami.inventory.entity.CustomerOrder;
import com.swami.inventory.entity.OrderItem;
import com.swami.inventory.entity.OrderStatus;
import com.swami.inventory.entity.Product;
import com.swami.inventory.exception.InsufficientStockException;
import com.swami.inventory.exception.ResourceNotFoundException;
import com.swami.inventory.repository.CustomerRepository;
import com.swami.inventory.repository.OrderRepository;
import com.swami.inventory.repository.ProductRepository;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    /**
     * Places an order. Everything runs in ONE transaction: if any line has
     * insufficient stock, the exception rolls back all stock changes.
     */
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id " + request.customerId()));

        CustomerOrder order = new CustomerOrder(customer);
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id " + line.productId()));

            if (product.getStockQuantity() < line.quantity()) {
                throw new InsufficientStockException("Insufficient stock for '" + product.getName()
                        + "'. Available: " + product.getStockQuantity() + ", requested: " + line.quantity());
            }

            product.setStockQuantity(product.getStockQuantity() - line.quantity());

            OrderItem item = new OrderItem(product, line.quantity(), product.getPrice());
            order.addItem(item);
            total = total.add(item.getLineTotal());
        }

        order.setTotalAmount(total);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return OrderResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> list(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderResponse::from);
    }

    /** Cancels an order and puts the stock back. */
    @Transactional
    public OrderResponse cancel(Long id) {
        CustomerOrder order = findOrThrow(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order " + id + " is already cancelled");
        }
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return OrderResponse.from(order);
    }

    private CustomerOrder findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
    }
}
