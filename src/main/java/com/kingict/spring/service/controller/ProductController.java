package com.kingict.spring.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.service.ProductService;
import org.apache.coyote.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return product;
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return productService.getCategories();
    }

    @GetMapping("/filter")
    public List<Product> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "0") Double lower,
            @RequestParam(required = false, defaultValue = "" + Double.MAX_VALUE) Double upper) {

        // validate price range
        if (lower < 0) {
            throw new IllegalArgumentException("Lower value cannot be negative");
        }
        if (upper < lower) {
            throw new IllegalArgumentException("Upper value cannot be negative");
        }

        // category validation
        if (category != null && !category.isEmpty()) {
            // easier to use if it's case insensitive
            category = category.toLowerCase();
            List<String> allCategories = productService.getCategories()
                    .stream()
                    .map(String::toLowerCase)
                    .toList();

            if (!allCategories.contains(category)) {
                throw new IllegalArgumentException("Invalid category: " + category);
            }
        }

        return productService.filterProducts(category, lower, upper);
    }
}
