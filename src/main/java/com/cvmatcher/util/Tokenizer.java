package com.cvmatcher.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Normalizes free text into a list of meaningful lowercase tokens. */
public final class Tokenizer {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the", "and", "or", "but", "is", "are", "was", "were",
            "to", "of", "in", "on", "at", "for", "with", "by", "as", "be",
            "this", "that", "it", "we", "you", "your", "will", "our", "have",
            "has", "from", "must", "should", "can", "we're", "who"
    );

    private Tokenizer() {}

    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return List.of();
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9+#. ]", " ");
        return Arrays.stream(cleaned.split("\\s+"))
                .map(String::trim)
                .filter(w -> !w.isEmpty() && !STOP_WORDS.contains(w) && w.length() > 1)
                .collect(Collectors.toList());
    }

    public static String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase().trim().replaceAll("\\s+", " ");
    }
}
