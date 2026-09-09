package com.cvmatcher.service;

import com.cvmatcher.model.JobNotice;

/** Value object returned by DuplicateCheckService. */
public class DuplicateResult {

    private final boolean duplicate;
    private final JobNotice matchedNotice;

    private DuplicateResult(boolean duplicate, JobNotice matchedNotice) {
        this.duplicate = duplicate;
        this.matchedNotice = matchedNotice;
    }

    public static DuplicateResult noDuplicate() {
        return new DuplicateResult(false, null);
    }

    public static DuplicateResult duplicateOf(JobNotice notice) {
        return new DuplicateResult(true, notice);
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public JobNotice getMatchedNotice() {
        return matchedNotice;
    }

    public String describe() {
        if (!duplicate) return "No duplicate found.";
        return "This notice already exists as: " + matchedNotice.getCompany() + " - "
                + matchedNotice.getTitle() + " (ID: " + matchedNotice.getId() + ")";
    }
}
