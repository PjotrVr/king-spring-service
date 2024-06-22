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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private static final String PRODUCTS_URL = "https://dummyjson.com/products";
    private static final String CATEGORIES_URL = "https://dummyjson.com/products/categories";
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProductService() {
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

    public Product getProductById(Long id) {
        List<Product> products = getProducts();
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
}