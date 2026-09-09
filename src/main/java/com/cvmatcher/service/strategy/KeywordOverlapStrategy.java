package com.cvmatcher.service.strategy;

import com.cvmatcher.model.Cv;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.util.Tokenizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Simple, fast baseline: fraction of the job notice's key terms found in the CV. */
public class KeywordOverlapStrategy implements ScoringStrategy {

    @Override
    public double score(Cv cv, JobNotice notice) {
        List<String> noticeTokens = Tokenizer.tokenize(notice.toSearchableText());
        if (noticeTokens.isEmpty()) return 0.0;

        Set<String> requiredTerms = new HashSet<>(noticeTokens);
        Set<String> cvTerms = new HashSet<>(Tokenizer.tokenize(cv.toSearchableText()));

        long matched = requiredTerms.stream().filter(cvTerms::contains).count();
        return (double) matched / requiredTerms.size();
    }
}
