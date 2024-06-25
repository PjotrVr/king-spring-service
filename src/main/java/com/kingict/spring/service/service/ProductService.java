package com.kingict.spring.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.repository.ProductRepository;
import com.kingict.spring.service.utils.ProductScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

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

    /**
     * Fetches products from online URL API.
     *
     * @return list of products
     */
    private List<Product> fetchProductsFromApi() {
        logger.info("Fetching products from API: {}", PRODUCTS_URL);
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
            logger.error("Error parsing product data", e);
        }

        return products;
    }

    /**
     * Fetches products from database if they exist, otherwise from URL API.
     *
     * @return list of products
     */
    public List<Product> getProducts() {
        logger.info("Fetching all products from the database");
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            logger.info("No products found in database, fetching from API");
            products = fetchProductsFromApi();
            productRepository.saveAll(products);
            logger.info("Products saved to database");
        }
        return products;
    }

    /**
     * Makes a list of categories from all products.
     *
     * @return list of categories
     */
    public List<String> getCategories() {
        logger.info("Fetching all categories from the database");
        // gather all categories and then remove duplicates
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Finds a product in repository by its id.
     *
     * @param id id that will be used to find a specific product
     * @return product specified by id if it exists, otherwise null
     */
    public Product getProductById(Long id) {
        logger.info("Fetching product with id {}", id);
        return productRepository.findById(id).orElse(null);
    }

    private Predicate<Product> createCategoryPredicate(String category) {
        return product -> category == null || category.isEmpty() || product.getCategory().equalsIgnoreCase(category);
    }

    private Predicate<Product> createPricePredicate(Double lowerPrice, Double upperPrice) {
        return product -> product.getPrice() >= lowerPrice && product.getPrice() <= upperPrice;
    }

    /**
     * Filters all products by three criteria.
     *
     * @param category if specified, only products of that category will pass, otherwise use all categories
     * @param lowerPrice all products must be above or equal to this value
     * @param upperPrice all products must be below or equal to this value
     * @return products that pass all three criteria
     */
    public List<Product> filterProducts(String category, Double lowerPrice, Double upperPrice) {
        logger.info("Filtering products by category = {}, lower price = {}, upper price = {}", category, lowerPrice, upperPrice);
        return productRepository.findAll().stream()
                .filter(createCategoryPredicate(category))
                .filter(createPricePredicate(lowerPrice, upperPrice))
                .collect(Collectors.toList());
    }

    /**
     * Searches all products and matches the query to their name and description. <br>
     * Result is ordered list so first product is the one that matched the most with the query. <br>
     * Last product is one that matched the least (but still matched) with the query. <br>
     * Products that didn't match at all (had score 0) won't be included.
     *
     * @param query user search that will be matched to products
     * @return ordered list of products
     */
    public List<Product> searchProducts(String query) {
        logger.info("Searching products with query: {}", query);
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
