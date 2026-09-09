package com.cvmatcher.service;

import com.cvmatcher.model.GapRecommendation;

import java.util.ArrayList;
import java.util.List;

/** Accumulator object passed through the GapCheckHandler chain. */
public class GapReport {
    private final List<GapRecommendation> gaps = new ArrayList<>();

    public void addGap(String missingItem, String suggestion) {
        gaps.add(new GapRecommendation(missingItem, suggestion));
    }

    public List<GapRecommendation> getGaps() {
        return gaps;
    }

    public boolean hasGaps() {
        return !gaps.isEmpty();
    }
}
