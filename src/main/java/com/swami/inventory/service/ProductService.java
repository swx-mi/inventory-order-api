package com.swami.inventory.service;

import com.swami.inventory.dto.ProductRequest;
import com.swami.inventory.dto.ProductResponse;
import com.swami.inventory.entity.Product;
import com.swami.inventory.exception.DuplicateResourceException;
import com.swami.inventory.exception.ResourceNotFoundException;
import com.swami.inventory.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with sku '" + request.sku() + "' already exists");
        }
        Product product = new Product(request.sku(), request.name(), request.price(), request.stockQuantity());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(String name, Pageable pageable) {
        Page<Product> page = (name == null || name.isBlank())
                ? productRepository.findAll(pageable)
                : productRepository.findByNameContainingIgnoreCase(name, pageable);
        return page.map(ProductResponse::from);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        if (!product.getSku().equals(request.sku()) && productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with sku '" + request.sku() + "' already exists");
        }
        product.setSku(request.sku());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findOrThrow(id));
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }
}
