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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CacheService {
    @Value("${product.service.products.url}")
    private String PRODUCTS_URL;

    @Value("${product.service.categories.url}")
    private String CATEGORIES_URL;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CacheService() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    private String fetchJsonData(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } else {
            System.err.println("Failed to fetch JSON data. HTTP error code: " + statusCode);
        }
        return null;
    }

    @Cacheable("products")
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        try {
            String rawJson = fetchJsonData(PRODUCTS_URL);
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

    @Cacheable("categories")
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        try {
            String rawJson = fetchJsonData(CATEGORIES_URL);
            JsonNode rootNode = objectMapper.readTree(rawJson);
            for (JsonNode categoryNode : rootNode) {
                categories.add(categoryNode.path("name").asText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }
}
