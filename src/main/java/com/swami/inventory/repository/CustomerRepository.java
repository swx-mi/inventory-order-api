package com.swami.inventory.repository;

import com.swami.inventory.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
}
