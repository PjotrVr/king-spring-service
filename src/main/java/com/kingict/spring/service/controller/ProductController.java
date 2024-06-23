package com.kingict.spring.service.controller;

import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = productService.getProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = productService.getCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "0") Double lower,
            @RequestParam(required = false, defaultValue = "" + Double.MAX_VALUE) Double upper) {

        // validate price range
        if (lower < 0) {
            return ResponseEntity.badRequest().body("Lower value cannot be negative");
        }
        if (upper < lower) {
            return ResponseEntity.badRequest().body("Upper value cannot be lower than lower value");
        }

        // validate category
        if (category != null && !category.isEmpty()) {
            category = category.toLowerCase();
            List<String> allCategories = productService.getCategories()
                    .stream()
                    .map(String::toLowerCase)
                    .toList();

            if (!allCategories.contains(category)) {
                return ResponseEntity.badRequest().body("Invalid category: " + category);
            }
        }

        List<Product> filteredProducts = productService.filterProducts(category, lower, upper);
        if (filteredProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(filteredProducts);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty");
        }
        List<Product> searchResults = productService.searchProducts(query);
        if (searchResults.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(searchResults);
    }
}
