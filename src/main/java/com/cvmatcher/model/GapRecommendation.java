package com.cvmatcher.model;

public class GapRecommendation {
    private int id;
    private int matchResultId;
    private String missingItem;
    private String suggestion;

    public GapRecommendation() {}

    public GapRecommendation(String missingItem, String suggestion) {
        this.missingItem = missingItem;
        this.suggestion = suggestion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMatchResultId() { return matchResultId; }
    public void setMatchResultId(int matchResultId) { this.matchResultId = matchResultId; }
    public String getMissingItem() { return missingItem; }
    public void setMissingItem(String missingItem) { this.missingItem = missingItem; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
}
