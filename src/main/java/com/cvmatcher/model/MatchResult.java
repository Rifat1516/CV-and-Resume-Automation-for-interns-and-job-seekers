package com.cvmatcher.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {
    private int id;
    private int userId;
    private int jobNoticeId;
    private double score;
    private RankLevel rankLevel;
    private final List<GapRecommendation> gaps = new ArrayList<>();

    public MatchResult() {}

    public MatchResult(int userId, int jobNoticeId, double score) {
        this.userId = userId;
        this.jobNoticeId = jobNoticeId;
        this.score = score;
        this.rankLevel = RankLevel.fromScore(score);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getJobNoticeId() { return jobNoticeId; }
    public void setJobNoticeId(int jobNoticeId) { this.jobNoticeId = jobNoticeId; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public RankLevel getRankLevel() { return rankLevel; }
    public void setRankLevel(RankLevel rankLevel) { this.rankLevel = rankLevel; }
    public List<GapRecommendation> getGaps() { return gaps; }
}
