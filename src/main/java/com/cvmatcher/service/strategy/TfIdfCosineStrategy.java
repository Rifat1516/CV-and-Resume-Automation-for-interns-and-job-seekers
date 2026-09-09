package com.cvmatcher.service.strategy;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.util.CosineSimilarity;

/**
 * Stronger scoring strategy: treats the CV and the job notice as text
 * documents and measures cosine similarity between their term vectors.
 * Reuses the same CosineSimilarity utility as duplicate-notice detection.
 */
public class TfIdfCosineStrategy implements ScoringStrategy {

    @Override
    public double score(Cv cv, JobNotice notice) {
        return CosineSimilarity.compute(cv.toSearchableText(), notice.toSearchableText());
    }
}
