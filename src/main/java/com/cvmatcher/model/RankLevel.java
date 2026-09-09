package com.cvmatcher.model;

public enum RankLevel {
    HIGH,
    MEDIUM,
    LOW;

    public static RankLevel fromScore(double score) {
        if (score >= 0.75) return HIGH;
        if (score >= 0.40) return MEDIUM;
        return LOW;
    }
}
