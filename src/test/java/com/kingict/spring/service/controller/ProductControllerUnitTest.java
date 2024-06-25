package com.kingict.spring.service.controller;

import com.kingict.spring.service.model.Product;
import com.kingict.spring.service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product expensiveRedProduct;
    private List<Product> products;

    @BeforeEach
    public void setUp() {
        expensiveRedProduct = new Product(1L, "Red Product", "Expensive", "It is red.", 100.0, "http://example.com/red_product.jpg");
        Product cheapBlueProduct = new Product(2L, "Blue Product", "Cheap", "It is blue.", 50.0, "http://example.com/blue_product.jpg");
        products = Arrays.asList(expensiveRedProduct, cheapBlueProduct);
    }

    @Test
    public void should_return_all_products() throws Exception {
        when(productService.getProducts()).thenReturn(products);
        MockHttpServletRequestBuilder requestBuilder = get("/products");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Red Product"))
                .andExpect(jsonPath("$[1].name").value("Blue Product"));
    }

    @Test
    public void should_return_no_content_when_no_products() throws Exception {
        when(productService.getProducts()).thenReturn(Collections.emptyList());
        MockHttpServletRequestBuilder requestBuilder = get("/products");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    public void should_return_product_by_id() throws Exception {
        when(productService.getProductById(1L)).thenReturn(expensiveRedProduct);
        MockHttpServletRequestBuilder requestBuilder = get("/products/1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Red Product"));
    }

    @Test
    public void should_return_not_found_for_invalid_product_id() throws Exception {
        when(productService.getProductById(1L)).thenReturn(null);
        MockHttpServletRequestBuilder requestBuilder = get("/products/1");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void should_return_all_categories() throws Exception {
        List<String> categories = Arrays.asList("Expensive", "Cheap");
        when(productService.getCategories()).thenReturn(categories);
        MockHttpServletRequestBuilder requestBuilder = get("/products/categories");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("Expensive"))
                .andExpect(jsonPath("$[1]").value("Cheap"));
    }

    @Test
    public void should_return_no_content_when_no_categories() throws Exception {
        when(productService.getCategories()).thenReturn(Collections.emptyList());
        MockHttpServletRequestBuilder requestBuilder = get("/products/categories");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    public void should_return_expensive_products() throws Exception {
        List<Product> filteredProducts = List.of(expensiveRedProduct);
        when(productService.filterProducts("expensive", 50.0, 150.0)).thenReturn(filteredProducts);

        List<String> categories = Arrays.asList("Expensive", "Cheap");
        when(productService.getCategories()).thenReturn(categories);

        MockHttpServletRequestBuilder requestBuilder = get("/products/filter")
                .param("category", "Expensive")
                .param("lower", "50")
                .param("upper", "150");
        //ResultActions resultActions = mockMvc.perform(requestBuilder);
        ResultActions resultActions = mockMvc.perform(requestBuilder).andDo(print()); // Print response for debugging
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Red Product"));
    }

    @Test
    public void should_return_bad_request_for_invalid_price_range() throws Exception {
        MockHttpServletRequestBuilder requestBuilder1 = get("/products/filter")
                .param("lower", "-1")
                .param("upper", "100");
        ResultActions resultActions1 = mockMvc.perform(requestBuilder1);
        resultActions1.andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder requestBuilder2 = get("/products/filter")
                .param("lower", "150")
                .param("upper", "100");
        ResultActions resultActions2 = mockMvc.perform(requestBuilder2);
        resultActions2.andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_bad_request_for_invalid_category() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/products/filter")
                .param("category", "InvalidCategory");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void should_search_red_products() throws Exception {
        List<Product> searchResults = List.of(expensiveRedProduct);
        when(productService.searchProducts("Red")).thenReturn(searchResults);
        MockHttpServletRequestBuilder requestBuilder = get("/products/search")
                .param("query", "Red");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Red Product"));
    }

    @Test
    public void should_return_bad_request_for_empty_search_query() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/products/search")
                .param("query", "");
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isBadRequest());
    }
}
