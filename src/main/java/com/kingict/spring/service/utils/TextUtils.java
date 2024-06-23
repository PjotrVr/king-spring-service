package com.kingict.spring.service.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {
    public static List<String> tokenize(String text) {
        // splits text into sub words, everything is in lowercase
        // eg. "Hello World" -> ["hello, "world"]
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .collect(Collectors.toList());
    }
}
