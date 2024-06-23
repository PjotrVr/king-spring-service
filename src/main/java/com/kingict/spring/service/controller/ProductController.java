package com.kingict.spring.service.controller;

import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        logger.info("Fetching all products");
        List<Product> products = productService.getProducts();
        if (products.isEmpty()) {
            logger.info("No products found");
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with id {}", id);
        Product product = productService.getProductById(id);
        if (product == null) {
            logger.warn("Product with id {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        logger.info("Returning product with id {}", id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        logger.info("Fetching all categories");
        List<String> categories = productService.getCategories();
        if (categories.isEmpty()) {
            logger.info("No categories found");
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "0") Double lower,
            @RequestParam(required = false, defaultValue = "" + Double.MAX_VALUE) Double upper) {

        // validate price range
        if (lower < 0) {
            logger.warn("Invalid lower price: {}", lower);
            return ResponseEntity.badRequest().body("Lower value cannot be negative");
        }
        if (upper < lower) {
            logger.warn("Invalid price range: lower = {}, upper = {}", lower, upper);
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
                logger.warn("Invalid category: {}", category);
                return ResponseEntity.badRequest().body("Invalid category: " + category);
            }
        }

        logger.info("Filtering products by category = {}, lower = {}, upper = {}", category, lower, upper);
        List<Product> filteredProducts = productService.filterProducts(category, lower, upper);
        if (filteredProducts.isEmpty()) {
            logger.info("No products found for the given criteria");
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} filtered products", filteredProducts.size());
        return ResponseEntity.ok(filteredProducts);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            logger.warn("Empty search query");
            return ResponseEntity.badRequest().body("Query cannot be empty");
        }
        logger.info("Searching products with query: {}", query);
        List<Product> searchResults = productService.searchProducts(query);
        if (searchResults.isEmpty()) {
            logger.info("No products found for query: {}", query);
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning {} search results", searchResults.size());
        return ResponseEntity.ok(searchResults);
    }
}
