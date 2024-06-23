package com.kingict.spring.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingict.spring.service.model.Product;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final CacheService cacheService;

    @Autowired
    public ProductService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public List<Product> getProducts() {
        return cacheService.getProducts();
    }

    public List<String> getCategories() {
        return cacheService.getCategories();
    }

    public Product getProductById(Long id) {
        List<Product> products = getProducts();

        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    private Predicate<Product> createCategoryPredicate(String category) {
        return product -> category == null || category.isEmpty() || product.getCategory().equals(category);
    }

    private Predicate<Product> createPricePredicate(Double lowerPrice, Double upperPrice) {
        return product -> product.getPrice() >= lowerPrice && product.getPrice() <= upperPrice;
    }

    public List<Product> filterProducts(String category, Double lowerPrice, Double upperPrice) {
        List<Product> allProducts = getProducts();

        return allProducts.stream()
                .filter(createCategoryPredicate(category))
                .filter(createPricePredicate(lowerPrice, upperPrice))
                .toList();
    }
}
