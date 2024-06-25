package com.kingict.spring.service.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

class TextUtilsUnitTest {

    @Test
    void should_remove_special_characters_and_spaces_in_lowercase() {
        String text = "    Complex       string, with: punctuation and random characters*';..~!.     ";
        List<String> expected = Arrays.asList("complex", "string", "with", "punctuation", "and", "random", "characters");
        Assertions.assertThat(TextUtils.tokenize(text)).isEqualTo(expected);
    }

    @Test
    void should_return_empty_list() {
        String text = "";
        List<String> expected = List.of();
        Assertions.assertThat(TextUtils.tokenize(text)).isEqualTo(expected);
    }
}
