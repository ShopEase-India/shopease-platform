package com.shopease.productservice.service.impl;

import com.shopease.productservice.dto.ProductDto;
import com.shopease.productservice.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final List<ProductDto> PRODUCTS = List.of(

            ProductDto.builder()
                    .id(1L)
                    .name("MacBook Pro 16")
                    .description("Apple M4 Pro Laptop")
                    .price(new BigDecimal("249999.00"))
                    .quantity(15)
                    .category("Electronics")
                    .build(),

            ProductDto.builder()
                    .id(2L)
                    .name("iPhone 17 Pro")
                    .description("Apple Smartphone")
                    .price(new BigDecimal("129999.00"))
                    .quantity(30)
                    .category("Electronics")
                    .build(),

            ProductDto.builder()
                    .id(3L)
                    .name("Sony WH-1000XM6")
                    .description("Wireless Noise Cancelling Headphones")
                    .price(new BigDecimal("34999.00"))
                    .quantity(20)
                    .category("Accessories")
                    .build()

    );

    @Override
    public List<ProductDto> getAllProducts() {
        return PRODUCTS;
    }

    @Override
    public ProductDto getProductById(Long id) {

        return PRODUCTS.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(
                        ProductDto.builder()
                                .id(id)
                                .name("Unknown Product")
                                .description("Product not found")
                                .price(BigDecimal.ZERO)
                                .quantity(0)
                                .category("Unknown")
                                .build()
                );
    }

}