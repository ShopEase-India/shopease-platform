package com.shopease.productservice.controller;

import com.shopease.common.response.ApiResponse;
import com.shopease.productservice.dto.ProductDto;
import com.shopease.productservice.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Product operations.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns all products.
     */
    @GetMapping
    public ApiResponse<List<ProductDto>> getAllProducts() {

        return ApiResponse.success(
                productService.getAllProducts(),
                "Products retrieved successfully"
        );
    }

    /**
     * Returns a product by ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDto> getProductById(@PathVariable("id") Long id) {

        return ApiResponse.success(
                productService.getProductById(id),
                "Product retrieved successfully"
        );
    }

}