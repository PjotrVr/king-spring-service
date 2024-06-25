package com.kingict.spring.service.service;

import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.repository.ProductRepository;
import jdk.jfr.Description;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductService productService;

    private static Product expensiveRedProduct;
    private static Product expensiveBlueProduct;
    private static Product cheapBlueProduct;


    @BeforeAll
    public static void initialize() {
        expensiveRedProduct = new Product(1L, "Red Product", "Expensive", "It is red.", 100.0, "http://example.com/red_product.jpg");
        expensiveBlueProduct = new Product(2L, "Blue Product", "Expensive", "It is expensive blue.", 120.0, "http://example.com/blue_product1.jpg");
        cheapBlueProduct = new Product(3L, "Blue Product", "Cheap", "It is blue.", 10.0, "http://example.com/blue_product2.jpg");
    }

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
        ReflectionTestUtils.setField(productService, "PRODUCTS_URL", "http://example.com/api/products");
        ReflectionTestUtils.setField(productService, "restTemplate", restTemplate);
    }

    @Test
    void should_get_test_product_from_database() {
        when(productRepository.findAll()).thenReturn(List.of(expensiveRedProduct, cheapBlueProduct));

        List<Product> products = productService.getProducts();

        Assertions.assertThat(products).hasSize(2);
        Assertions.assertThat(products.get(0).getName()).isEqualTo("Red Product");
        Assertions.assertThat(products.get(1).getName()).isEqualTo("Blue Product");
    }

    @Test
    void should_get_test_product_from_url() throws Exception {
        // nothing should return from database
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // resulting json
        String rawJson = String.format("{\"products\":[{\"id\":%d,\"title\":\"%s\",\"description\":\"%s\",\"category\":\"%s\",\"price\":%.2f,\"thumbnail\":\"%s\"}," +
                        "{\"id\":%d,\"title\":\"%s\",\"description\":\"%s\",\"category\":\"%s\",\"price\":%.2f,\"thumbnail\":\"%s\"}]}",
                expensiveRedProduct.getId(), expensiveRedProduct.getName(), expensiveRedProduct.getDescription(), expensiveRedProduct.getCategory(), expensiveRedProduct.getPrice(), expensiveRedProduct.getImageUrl(),
                cheapBlueProduct.getId(), cheapBlueProduct.getName(), cheapBlueProduct.getDescription(), cheapBlueProduct.getCategory(), cheapBlueProduct.getPrice(), cheapBlueProduct.getImageUrl());

        // mock result for http request
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(rawJson);

        List<Product> products = productService.getProducts();

        Assertions.assertThat(products).hasSize(2);
        Assertions.assertThat(products.get(0)).isEqualTo(expensiveRedProduct);
        Assertions.assertThat(products.get(1)).isEqualTo(cheapBlueProduct);

        Mockito.verify(productRepository, Mockito.times(1)).saveAll(any(List.class));
    }

    @Test
    void should_get_product_by_id_1() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(expensiveRedProduct));

        Product foundProduct = productService.getProductById(1L);

        Assertions.assertThat(foundProduct).isNotNull();
        Assertions.assertThat(foundProduct).isEqualTo(expensiveRedProduct);
    }

    @Test
    void should_not_filter_anything() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(expensiveRedProduct, expensiveBlueProduct));

        List<Product> filteredProducts = productService.filterProducts("Expensive", 0.0, Double.MAX_VALUE);

        Assertions.assertThat(filteredProducts).hasSize(2);
        Assertions.assertThat(filteredProducts.get(0)).isEqualTo(expensiveRedProduct);
        Assertions.assertThat(filteredProducts.get(1)).isEqualTo(expensiveBlueProduct);
    }

    @Test
    void should_filter_everything() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(expensiveRedProduct, expensiveBlueProduct));
        List<Product> filteredProducts = productService.filterProducts("Cheap", 0.0, Double.MAX_VALUE);

        Assertions.assertThat(filteredProducts).hasSize(0);
    }

    @Test
    void should_include_products_over_15() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(cheapBlueProduct, expensiveBlueProduct));

        List<Product> filteredProducts = productService.filterProducts(null, 15.0, Double.MAX_VALUE);

        Assertions.assertThat(filteredProducts).hasSize(1);
        Assertions.assertThat(filteredProducts.get(0)).isEqualTo(expensiveBlueProduct);
    }

    @Test
    void should_include_products_between_10_and_100() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(cheapBlueProduct, expensiveBlueProduct, expensiveRedProduct));

        List<Product> filteredProducts = productService.filterProducts(null, 10.0, 100.0);

        Assertions.assertThat(filteredProducts).hasSize(2);
        Assertions.assertThat(filteredProducts.get(0)).isEqualTo(cheapBlueProduct);
        Assertions.assertThat(filteredProducts.get(1)).isEqualTo(expensiveRedProduct);
    }

    @Test
    void should_include_red_product() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(cheapBlueProduct, expensiveBlueProduct, expensiveRedProduct));

        List<Product> searchResults = productService.searchProducts("Red");

        // blue products didn't match at all so there is only red product in the list
        Assertions.assertThat(searchResults).hasSize(1);
        Assertions.assertThat(searchResults.get(0)).isEqualTo(expensiveRedProduct);
    }

    @Test
    @Description("Should return blue products where first product is more expensive because it has higher score.")
    void should_include_blue_products() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(cheapBlueProduct, expensiveBlueProduct, expensiveRedProduct));

        List<Product> searchResults = productService.searchProducts(" Expensive Blue ");

        // first product is expensive blue and second one is cheap blue because of keyword "Expensive"
        Assertions.assertThat(searchResults).hasSize(2);
        Assertions.assertThat(searchResults.get(0)).isEqualTo(expensiveBlueProduct);
        Assertions.assertThat(searchResults.get(1)).isEqualTo(cheapBlueProduct);
    }
}
