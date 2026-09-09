package com.cvmatcher.service;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.service.chain.EducationGapHandler;
import com.cvmatcher.service.chain.ExperienceGapHandler;
import com.cvmatcher.service.chain.GapCheckHandler;
import com.cvmatcher.service.chain.SkillGapHandler;

/** Builds and runs the GapCheckHandler chain. */
public class GapAnalyzer {

    public GapReport analyze(Cv cv, JobNotice notice) {
        GapCheckHandler chain = new SkillGapHandler();
        chain.setNext(new EducationGapHandler())
             .setNext(new ExperienceGapHandler());

        GapReport report = new GapReport();
        chain.handle(cv, notice, report);
        return report;
    }
}
