package com.swami.inventory.dto;

import com.swami.inventory.entity.Customer;

public record CustomerResponse(Long id, String name, String email) {

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getName(), c.getEmail());
    }
}
