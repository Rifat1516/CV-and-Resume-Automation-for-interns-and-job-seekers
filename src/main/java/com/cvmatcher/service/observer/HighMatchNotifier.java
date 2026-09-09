package com.cvmatcher.service.observer;

import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.MatchResult;
import com.cvmatcher.model.User;

/**
 * Simplest concrete observer: logs the event. In the JavaFX UI this can
 * be swapped/extended for a toast notification or badge update without
 * MatchingService knowing or caring.
 */
public class HighMatchNotifier implements MatchObserver {

    @Override
    public void onHighMatch(User user, JobNotice notice, MatchResult result) {
        System.out.printf("[notification] %s: strong match with %s at %s (score=%.2f)%n",
                user.getFullName(), notice.getTitle(), notice.getCompany(), result.getScore());
    }
}
