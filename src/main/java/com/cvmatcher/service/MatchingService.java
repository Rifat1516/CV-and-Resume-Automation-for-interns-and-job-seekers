package com.cvmatcher.service;

import com.cvmatcher.dao.MatchResultDao;
import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.MatchResult;
import com.cvmatcher.model.RankLevel;
import com.cvmatcher.model.User;
import com.cvmatcher.service.observer.MatchObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates ranking a user's CV against one or more job notices.
 * Runs each pair through the Template-Method JobProcessor and notifies
 * MatchObservers whenever a HIGH match is found.
 */
public class MatchingService {

    private final JobProcessor jobProcessor;
    private final MatchResultDao matchResultDao;
    private final List<MatchObserver> observers = new ArrayList<>();

    public MatchingService(JobProcessor jobProcessor, MatchResultDao matchResultDao) {
        this.jobProcessor = jobProcessor;
        this.matchResultDao = matchResultDao;
    }

    public void addObserver(MatchObserver observer) {
        observers.add(observer);
    }

    public List<MatchResult> rankNotices(User user, Cv cv, List<JobNotice> notices) {
        List<MatchResult> results = new ArrayList<>();
        for (JobNotice notice : notices) {
            if (!notice.getState().isRankable()) {
                continue; // closed notices are skipped, not scored
            }
            MatchResult result = jobProcessor.process(cv, notice);
            matchResultDao.save(result);
            results.add(result);

            if (result.getRankLevel() == RankLevel.HIGH) {
                observers.forEach(o -> o.onHighMatch(user, notice, result));
            }
        }
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return results;
    }
}
