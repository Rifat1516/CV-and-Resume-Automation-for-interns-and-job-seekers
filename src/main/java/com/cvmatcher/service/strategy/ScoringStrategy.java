package com.cvmatcher.service.strategy;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;

/**
 * Strategy pattern: interchangeable CV-to-job scoring algorithms.
 * MatchingService depends only on this interface, so a new algorithm
 * (e.g. a smarter one later) plugs in without touching MatchingService,
 * GapAnalyzer, or the UI.
 */
public interface ScoringStrategy {
    double score(Cv cv, JobNotice notice);
}
