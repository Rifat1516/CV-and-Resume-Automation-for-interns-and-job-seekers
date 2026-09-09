package com.cvmatcher.service.chain;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.service.GapReport;

/**
 * Chain of Responsibility: each handler independently checks one
 * dimension of the CV against the job notice (skills, experience,
 * education, ...) and appends findings to the shared GapReport.
 * New checks (e.g. language requirements) can be added by writing a
 * new handler and inserting it into the chain -- no existing handler
 * needs to change.
 */
public abstract class GapCheckHandler {

    private GapCheckHandler next;

    public GapCheckHandler setNext(GapCheckHandler next) {
        this.next = next;
        return next;
    }

    public final void handle(Cv cv, JobNotice notice, GapReport report) {
        check(cv, notice, report);
        if (next != null) {
            next.handle(cv, notice, report);
        }
    }

    protected abstract void check(Cv cv, JobNotice notice, GapReport report);
}
