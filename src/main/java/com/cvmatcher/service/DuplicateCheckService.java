package com.cvmatcher.service;

import com.cvmatcher.dao.JobNoticeDao;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.util.CosineSimilarity;
import com.cvmatcher.util.Tokenizer;

import java.util.List;

/**
 * Two-tier duplicate detection for newly uploaded job notices:
 *  1. Exact match on normalized company+title (fast, catches copy-pasted reposts)
 *  2. Fuzzy similarity on the full description, reusing the same
 *     CosineSimilarity utility that TfIdfCosineStrategy uses for CV matching.
 */
public class DuplicateCheckService {

    private static final double SIMILARITY_THRESHOLD = 0.85;

    private final JobNoticeDao jobNoticeDao;

    public DuplicateCheckService(JobNoticeDao jobNoticeDao) {
        this.jobNoticeDao = jobNoticeDao;
    }

    public DuplicateResult check(JobNotice candidate) {
        List<JobNotice> existing = jobNoticeDao.findAll();
        String candidateKey = Tokenizer.normalize(candidate.getCompany() + " " + candidate.getTitle());

        // Tier 1: exact normalized key match
        for (JobNotice notice : existing) {
            String existingKey = Tokenizer.normalize(notice.getCompany() + " " + notice.getTitle());
            if (existingKey.equals(candidateKey)) {
                return DuplicateResult.duplicateOf(notice);
            }
        }

        // Tier 2: fuzzy similarity across the full structured notice (title, location,
        // experience, description, and every responsibility/requirement bullet)
        for (JobNotice notice : existing) {
            double similarity = CosineSimilarity.compute(candidate.toSearchableText(), notice.toSearchableText());
            if (similarity >= SIMILARITY_THRESHOLD) {
                return DuplicateResult.duplicateOf(notice);
            }
        }

        return DuplicateResult.noDuplicate();
    }
}
