package com.cvmatcher.model;

public class CvHighlight {
    private int id;
    private int cvId;
    private CvHighlightCategory category;
    private String description;

    public CvHighlight() {}

    public CvHighlight(CvHighlightCategory category, String description) {
        this.category = category;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public CvHighlightCategory getCategory() { return category; }
    public void setCategory(CvHighlightCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
