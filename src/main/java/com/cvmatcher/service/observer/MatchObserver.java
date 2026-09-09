package com.cvmatcher.service.observer;

import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.MatchResult;
import com.cvmatcher.model.User;

/**
 * Observer pattern: decouples the scoring logic (MatchingService) from
 * whatever should happen when a user gets a high match (UI toast, log,
 * future email notification, etc).
 */
public interface MatchObserver {
    void onHighMatch(User user, JobNotice notice, MatchResult result);
}
