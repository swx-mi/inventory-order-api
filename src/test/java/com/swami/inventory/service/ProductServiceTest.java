package com.swami.inventory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.swami.inventory.dto.ProductRequest;
import com.swami.inventory.entity.Product;
import com.swami.inventory.exception.DuplicateResourceException;
import com.swami.inventory.exception.ResourceNotFoundException;
import com.swami.inventory.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void create_withDuplicateSku_throwsConflict() {
        when(productRepository.existsBySku("PEN-1")).thenReturn(true);

        ProductRequest request = new ProductRequest("PEN-1", "Pen", new BigDecimal("10.00"), 5);

        assertThrows(DuplicateResourceException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getById_whenMissing_throwsNotFound() {
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(42L));
    }
}
