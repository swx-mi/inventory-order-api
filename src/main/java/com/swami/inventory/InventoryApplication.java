package com.swami.inventory;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Inventory & Order Management API")
                .version("1.0.0")
                .description("REST API for products, customers and orders with automatic stock management"));
    }
}
