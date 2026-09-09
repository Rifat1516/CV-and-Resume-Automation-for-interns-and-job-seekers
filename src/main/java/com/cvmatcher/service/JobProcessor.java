package com.cvmatcher.service;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.MatchResult;
import com.cvmatcher.model.RankLevel;
import com.cvmatcher.service.strategy.ScoringStrategy;

/**
 * Template Method: fixes the overall pipeline for turning a (Cv, JobNotice)
 * pair into a ranked, gap-annotated MatchResult, while delegating the
 * swappable step (the actual scoring) to a ScoringStrategy.
 */
public class JobProcessor {

    private final ScoringStrategy scoringStrategy;
    private final GapAnalyzer gapAnalyzer;

    public JobProcessor(ScoringStrategy scoringStrategy, GapAnalyzer gapAnalyzer) {
        this.scoringStrategy = scoringStrategy;
        this.gapAnalyzer = gapAnalyzer;
    }

    /** The fixed skeleton: score -> rank -> analyze gaps -> assemble result. */
    public final MatchResult process(Cv cv, JobNotice notice) {
        double score = scoreStep(cv, notice);
        RankLevel rank = rankStep(score);

        MatchResult result = new MatchResult(cv.getUserId(), notice.getId(), score);
        result.setRankLevel(rank);

        GapReport gapReport = gapAnalysisStep(cv, notice);
        result.getGaps().addAll(gapReport.getGaps());

        return result;
    }

    protected double scoreStep(Cv cv, JobNotice notice) {
        return scoringStrategy.score(cv, notice);
    }

    protected RankLevel rankStep(double score) {
        return RankLevel.fromScore(score);
    }

    protected GapReport gapAnalysisStep(Cv cv, JobNotice notice) {
        return gapAnalyzer.analyze(cv, notice);
    }
}
