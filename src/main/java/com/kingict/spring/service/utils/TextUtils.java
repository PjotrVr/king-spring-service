package com.kingict.spring.service.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {
    /**
     * Tokenizes input text into a list of lowercase words that only include alphanumeric characters.
     *
     * @param text input text that will be tokenized
     * @return a list of lowercase words that represent tokens from the input text
     */
    public static List<String> tokenize(String text) {
        String cleanedText = removeNonAlphanumericCharacters(text).trim().toLowerCase();

        // special case, without this function will return [""], but with this it returns []
        // makes more sense that it is this way
        if (cleanedText.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(cleanedText.split("\\W+"))
                .collect(Collectors.toList());
    }

    /**
     * Removes all characters except uppercase and lowercase letters, numbers and spaces.
     *
     * @param text input text that from which we remove non-alphanumeric characters
     * @return clean text with only alphanumeric characters
     */
    public static String removeNonAlphanumericCharacters(String text) {
        return text.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

}
