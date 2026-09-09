package com.cvmatcher.service.chain;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.Experience;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.service.GapReport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExperienceGapHandler extends GapCheckHandler {

    private static final Pattern YEARS_PATTERN = Pattern.compile("(\\d+)\\+?\\s*(years|yrs)");

    @Override
    protected void check(Cv cv, JobNotice notice, GapReport report) {
        String experienceText = notice.getExperienceRequired() != null
                ? notice.getExperienceRequired()
                : notice.toSearchableText();
        Matcher matcher = YEARS_PATTERN.matcher(experienceText.toLowerCase());
        if (!matcher.find()) return;

        int requiredYears = Integer.parseInt(matcher.group(1));
        int cvYears = estimateTotalYears(cv);

        if (cvYears < requiredYears) {
            report.addGap(
                    "Experience: " + requiredYears + "+ years required",
                    "Your CV shows roughly " + cvYears + " year(s) of experience; consider highlighting additional relevant work."
            );
        }
    }

    private int estimateTotalYears(Cv cv) {
        int total = 0;
        for (Experience e : cv.getExperienceList()) {
            if (e.getStartYear() != null && e.getEndYear() != null) {
                total += Math.max(0, e.getEndYear() - e.getStartYear());
            }
        }
        return total;
    }
}
