package com.cvmatcher.service;

import com.cvmatcher.model.GapRecommendation;

import java.util.ArrayList;
import java.util.List;

/**
 * Accumulator object passed through the GapCheckHandler chain.
 * Collects and manages identified gaps and their corresponding recommendations
 * during the CV evaluation process.
 */
public class GapReport {
    
    private final List<GapRecommendation> gaps = new ArrayList<>();

    /**
     * Adds a new gap recommendation to the report.
     *
     * @param missingItem The skill, experience, or attribute missing from the CV.
     * @param suggestion  Actionable advice or a suggestion to bridge the identified gap.
     */
    public void addGap(final String missingItem, final String suggestion) {
        this.gaps.add(new GapRecommendation(missingItem, suggestion));
    }

    /**
     * Retrieves the list of all identified gaps.
     *
     * @return A list of {@link GapRecommendation} objects.
     */
    public List<GapRecommendation> getGaps() {
        return this.gaps;
    }

    /**
     * Checks if there are any gaps recorded in this report.
     *
     * @return true if there is at least one gap recommendation, false otherwise.
     */
    public boolean hasGaps() {
        return !this.gaps.isEmpty();
    }
}

