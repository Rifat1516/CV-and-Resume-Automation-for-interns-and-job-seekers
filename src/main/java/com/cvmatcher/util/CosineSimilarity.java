package com.cvmatcher.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shared term-frequency cosine similarity math.
 * Reused by both TfIdfCosineStrategy (CV-to-job scoring) and
 * DuplicateCheckService (fuzzy notice comparison), so one algorithm
 * change benefits both features.
 */
public final class CosineSimilarity {

    private CosineSimilarity() {}

    public static double compute(String textA, String textB) {
        List<String> tokensA = Tokenizer.tokenize(textA);
        List<String> tokensB = Tokenizer.tokenize(textB);
        if (tokensA.isEmpty() || tokensB.isEmpty()) return 0.0;

        Map<String, Integer> freqA = termFrequency(tokensA);
        Map<String, Integer> freqB = termFrequency(tokensB);

        Set<String> vocabulary = new HashSet<>();
        vocabulary.addAll(freqA.keySet());
        vocabulary.addAll(freqB.keySet());

        double dot = 0.0, magA = 0.0, magB = 0.0;
        for (String term : vocabulary) {
            int a = freqA.getOrDefault(term, 0);
            int b = freqB.getOrDefault(term, 0);
            dot += a * b;
            magA += a * a;
            magB += b * b;
        }
        if (magA == 0 || magB == 0) return 0.0;
        return dot / (Math.sqrt(magA) * Math.sqrt(magB));
    }

    private static Map<String, Integer> termFrequency(List<String> tokens) {
        Map<String, Integer> freq = new HashMap<>();
        for (String t : tokens) freq.merge(t, 1, Integer::sum);
        return freq;
    }
}
