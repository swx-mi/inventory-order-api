package com.swami.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private final Customer customer = new Customer("Asha", "asha@example.com");

    @Test
    void placeOrder_reducesStock_andCalculatesTotal() {
        Product product = new Product("PEN-1", "Pen", new BigDecimal("100.00"), 10);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderRequest request = new OrderRequest(1L, List.of(new OrderItemRequest(5L, 3)));
        OrderResponse response = orderService.placeOrder(request);

        assertEquals(7, product.getStockQuantity());
        assertEquals(new BigDecimal("300.00"), response.totalAmount());
        assertEquals(OrderStatus.PLACED, response.status());
    }

    @Test
    void placeOrder_withInsufficientStock_throwsAndSavesNothing() {
        Product product = new Product("PEN-1", "Pen", new BigDecimal("100.00"), 2);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        OrderRequest request = new OrderRequest(1L, List.of(new OrderItemRequest(5L, 5)));

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(request));
        assertEquals(2, product.getStockQuantity());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_withUnknownCustomer_throwsNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        OrderRequest request = new OrderRequest(99L, List.of(new OrderItemRequest(5L, 1)));

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(request));
    }

    @Test
    void cancel_restoresStock_andMarksOrderCancelled() {
        Product product = new Product("PEN-1", "Pen", new BigDecimal("100.00"), 7);
        CustomerOrder order = new CustomerOrder(customer);
        order.addItem(new OrderItem(product, 3, new BigDecimal("100.00")));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.cancel(10L);

        assertEquals(10, product.getStockQuantity());
        assertEquals(OrderStatus.CANCELLED, response.status());
    }

    @Test
    void cancel_alreadyCancelledOrder_throws() {
        CustomerOrder order = new CustomerOrder(customer);
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancel(10L));
    }
}
