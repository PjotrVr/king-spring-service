package com.kingict.spring.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.repository.ProductRepository;
import com.kingict.spring.service.utils.ProductScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${product.service.products.url}")
    private String PRODUCTS_URL;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private List<Product> fetchProductsFromApi() {
        String rawJson = restTemplate.getForObject(PRODUCTS_URL, String.class);
        List<Product> products = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(rawJson);
            JsonNode productsNode = rootNode.path("products");

            if (productsNode.isArray()) {
                for (JsonNode productNode : productsNode) {
                    Product product = new Product();
                    product.setId(productNode.path("id").asLong());
                    product.setName(productNode.path("title").asText());
                    product.setDescription(productNode.path("description").asText());
                    product.setCategory(productNode.path("category").asText());
                    product.setPrice(productNode.path("price").asDouble());
                    product.setImageUrl(productNode.path("thumbnail").asText());
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }


    public List<Product> getProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            products = fetchProductsFromApi();
            productRepository.saveAll(products);
        }
        return products;
    }

    public List<String> getCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    private Predicate<Product> createCategoryPredicate(String category) {
        return product -> category == null || category.isEmpty() || product.getCategory().equalsIgnoreCase(category);
    }

    private Predicate<Product> createPricePredicate(Double lowerPrice, Double upperPrice) {
        return product -> product.getPrice() >= lowerPrice && product.getPrice() <= upperPrice;
    }

    public List<Product> filterProducts(String category, Double lowerPrice, Double upperPrice) {
        return productRepository.findAll().stream()
                .filter(createCategoryPredicate(category))
                .filter(createPricePredicate(lowerPrice, upperPrice))
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findAll().stream()
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
