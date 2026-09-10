package com.cvmatcher.service;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.service.chain.EducationGapHandler;
import com.cvmatcher.service.chain.ExperienceGapHandler;
import com.cvmatcher.service.chain.GapCheckHandler;
import com.cvmatcher.service.chain.SkillGapHandler;

/**
 * Builds and executes the GapCheckHandler chain.
 * Evaluates a candidate's CV against a Job Notice to identify missing
 * skills, education, or experience, aggregating the findings into a GapReport.
 */
public class GapAnalyzer {

    /**
     * Analyzes a CV against a specific job notice using a chain of responsibility.
     *
     * @param cv     The Curriculum Vitae of the candidate being evaluated.
     * @param notice The Job Notice containing the target requirements.
     * @return A {@link GapReport} containing all identified gaps and recommendations.
     */
    public GapReport analyze(final Cv cv, final JobNotice notice) {
        
        // Initialize and link the chain of responsibility handlers
        final GapCheckHandler chain = new SkillGapHandler();
        
        chain.setNext(new EducationGapHandler())
             .setNext(new ExperienceGapHandler());

        // Prepare the accumulator object to collect findings
        final GapReport report = new GapReport();
        
        // Execute the evaluation chain
        chain.handle(cv, notice, report);
        
        return report;
    }
}
