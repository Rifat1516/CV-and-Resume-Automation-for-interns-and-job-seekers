package com.cvmatcher.dao;

import com.cvmatcher.model.MatchResult;
import java.util.List;

public interface MatchResultDao {
    MatchResult save(MatchResult result);
    List<MatchResult> findByUserId(int userId);
}
