package com.cvmatcher.model;

public class JobRequirement {
    private int id;
    private int jobNoticeId;
    private RequirementCategory category;
    private String description;

    public JobRequirement() {}

    public JobRequirement(RequirementCategory category, String description) {
        this.category = category;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getJobNoticeId() { return jobNoticeId; }
    public void setJobNoticeId(int jobNoticeId) { this.jobNoticeId = jobNoticeId; }
    public RequirementCategory getCategory() { return category; }
    public void setCategory(RequirementCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
