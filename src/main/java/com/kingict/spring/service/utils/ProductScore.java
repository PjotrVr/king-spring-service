package com.kingict.spring.service.utils;

import com.kingict.spring.service.model.Product;

import java.util.List;

public class ProductScore {

    /**
     * Calculates similarity score for a query and a product.
     * Products matches in name are valued more than matches in description by a factor of k.
     *
     * @param product product whose tokenized name and description will be matched with tokenized query
     * @param query users query when searching
     * @return similarity score
     */
    public static int calculateScore(Product product, String query) {
        List<String> queryTokens = TextUtils.tokenize(query);
        List<String> productNameTokens = TextUtils.tokenize(product.getName());
        List<String> productDescriptionTokens = TextUtils.tokenize(product.getDescription());

        int nameMatches = calculateBidirectionalMatches(productNameTokens, queryTokens);
        int descriptionMatches = calculateBidirectionalMatches(productDescriptionTokens, queryTokens);

        return nameMatches * 2 + descriptionMatches;
    }

    /**
     * Calculates bidirectional matching for two queries. <br>
     * It checks if token1 is subset of token2 and if it is true we count that. <br>
     * It also checks if token2 is subset of token1 and if it is true count that. <br>
     * If tokens are exactly the same they will be counted two times, but that is desired behaviour
     * because we prioritize such queries more.
     *
     * @param tokens1 first tokenized query
     * @param tokens2 second tokenized query
     * @return score that shows how much two queries match
     */
    private static int calculateBidirectionalMatches(List<String> tokens1, List<String> tokens2) {
        int matches =  (int) tokens1.stream()
                .filter(t1 -> tokens2.stream().anyMatch(t1::contains))
                .count();
        matches += (int) tokens2.stream()
                .filter(t2 -> tokens1.stream().anyMatch(t2::contains))
                .count();
        return matches;
    }
}
