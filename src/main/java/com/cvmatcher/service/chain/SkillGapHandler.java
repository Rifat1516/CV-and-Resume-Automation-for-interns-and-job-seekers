package com.cvmatcher.service.chain;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.service.GapReport;
import com.cvmatcher.util.Tokenizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillGapHandler extends GapCheckHandler {

    @Override
    protected void check(Cv cv, JobNotice notice, GapReport report) {
        Set<String> cvSkills = new HashSet<>();
        cv.getSkills().forEach(s -> cvSkills.add(s.getName().toLowerCase()));

        String qualificationText = notice.getQualifications().isEmpty()
                ? notice.toSearchableText()
                : notice.getQualifications().stream()
                        .map(com.cvmatcher.model.JobRequirement::getDescription)
                        .reduce("", (a, b) -> a + " " + b);

        List<String> noticeTokens = Tokenizer.tokenize(qualificationText);
        Set<String> missing = new HashSet<>();
        for (String token : noticeTokens) {
            boolean present = cvSkills.stream().anyMatch(skill -> skill.contains(token) || token.contains(skill));
            if (!present) missing.add(token);
        }

        // Keep this focused: only flag terms that look like meaningful skill keywords (length-based heuristic).
        missing.stream()
                .filter(term -> term.length() > 3)
                .limit(5)
                .forEach(term -> report.addGap(
                        "Skill: " + term,
                        "Consider adding experience or a project demonstrating \"" + term + "\""
                ));
    }
}
