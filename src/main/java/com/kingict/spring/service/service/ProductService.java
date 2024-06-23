package com.kingict.spring.service.service;

import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.utils.ProductScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
        return getProducts().stream().filter(product -> product.getId().equals(id)).findFirst().orElse(null);
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

    public List<Product> searchProducts(String query) {
        return getProducts().stream()
                .map(product -> new ScoredProduct(product, ProductScore.calculateScore(product, query)))
                .filter(scoredProduct -> scoredProduct.score > 0)
                .sorted(Comparator.comparingInt(ScoredProduct::score).reversed())
                .map(ScoredProduct::product)
                .collect(Collectors.toList());
    }

    private record ScoredProduct(Product product, int score) {
        public ScoredProduct {
            if (score < 0) {
                throw new IllegalArgumentException("Score cannot be less than zero");
            }
        }
    }
}
