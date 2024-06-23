package com.kingict.spring.service.utils;

import com.kingict.spring.service.model.Product;

import java.util.List;

public class ProductScore {

    // calculates weighted score
    // match in a product name is worth 2x than one in the description name
    public static int calculateScore(Product product, String query) {
        List<String> queryTokens = TextUtils.tokenize(query);
        List<String> productNameTokens = TextUtils.tokenize(product.getName());
        List<String> productDescriptionTokens = TextUtils.tokenize(product.getDescription());

        int nameMatches = (int) productNameTokens.stream().filter(queryTokens::contains).count();
        int descriptionMatches = (int) productDescriptionTokens.stream().filter(queryTokens::contains).count();

        return nameMatches * 2 + descriptionMatches;
    }
}
