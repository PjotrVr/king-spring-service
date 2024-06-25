package com.kingict.spring.service.utils;

import com.kingict.spring.service.model.Product;
import jdk.jfr.Description;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProductScoreTest {

    private static Product product;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        product = new Product();
        product.setName("Red Color Nail Polish");
        product.setDescription("Your favorite polisher.");
    }

    @Test
    public void should_return_0_because_no_matches() {
        String query = "black eyeliner";
        int expected = 0;
        int productScore = ProductScore.calculateScore(product, query);
        Assertions.assertThat(productScore).isEqualTo(expected);
    }

    @Test
    public void should_return_4_because_exact_word_match() {
        String query = "red";
        int expected = 4;
        int productScore = ProductScore.calculateScore(product, query);
        Assertions.assertThat(productScore).isEqualTo(expected);
    }

    @Test
    public void should_return_1_because_one_subset_description_match() {
        String query = "fav";
        int expected = 1;
        int productScore = ProductScore.calculateScore(product, query);
        Assertions.assertThat(productScore).isEqualTo(expected);
    }

    @Test
    public void should_return_2_because_one_subset_name_match() {
        // misspell of "colour"
        String query = "coloru";
        int expected = 2;
        int productScore = ProductScore.calculateScore(product, query);
        Assertions.assertThat(productScore).isEqualTo(expected);
    }

    @Test
    @Description("Should return 5 because of one full match in the name (2 + 2) and one subset match in description.")
    public void should_return_5_for_combination_of_matches() {
        String query = "polish";
        int expected = 5;
        int productScore = ProductScore.calculateScore(product, query);
        Assertions.assertThat(productScore).isEqualTo(expected);
    }

    @Test
    public void should_return_0_because_empty_query() {
        String query = "";
        int expected = 0;
        int productScore = ProductScore.calculateScore(product, query);
        Assertions.assertThat(productScore).isEqualTo(expected);
    }
}
