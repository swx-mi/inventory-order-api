package com.swami.inventory.service;

import com.swami.inventory.dto.CustomerRequest;
import com.swami.inventory.dto.CustomerResponse;
import com.swami.inventory.entity.Customer;
import com.swami.inventory.exception.DuplicateResourceException;
import com.swami.inventory.exception.ResourceNotFoundException;
import com.swami.inventory.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Customer with email '" + request.email() + "' already exists");
        }
        Customer saved = customerRepository.save(new Customer(request.name(), request.email()));
        return CustomerResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        return CustomerResponse.from(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> list(Pageable pageable) {
        return customerRepository.findAll(pageable).map(CustomerResponse::from);
    }
}
