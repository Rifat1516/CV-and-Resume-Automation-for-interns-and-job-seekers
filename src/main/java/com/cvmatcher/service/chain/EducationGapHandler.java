package com.cvmatcher.service.chain;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.service.GapReport;

public class EducationGapHandler extends GapCheckHandler {

    @Override
    protected void check(Cv cv, JobNotice notice, GapReport report) {
        String textLower = notice.toSearchableText().toLowerCase();
        boolean mentionsDegree = textLower.contains("degree") || textLower.contains("bachelor")
                || textLower.contains("master") || textLower.contains("bsc") || textLower.contains("msc");

        if (mentionsDegree && cv.getEducationList().isEmpty()) {
            report.addGap("Education", "This notice references a degree requirement, but your CV has no education entries.");
        }
    }
}
