package com.cvmatcher.model;

/**
 * A single named role within a job notice (e.g. "AI Engineer", "Web
 * Developer"), each with its own headcount and how many are filled --
 * so one notice like "hiring 10 engineers" can break down into several
 * differently-available sub-roles instead of one flat total/filled pair.
 */
public class JobPosition {
    private int id;
    private int jobNoticeId;
    private String roleName;
    private int totalCount;
    private int filledCount;

    public JobPosition() {}

    public JobPosition(String roleName, int totalCount, int filledCount) {
        this.roleName = roleName;
        this.totalCount = totalCount;
        this.filledCount = filledCount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getJobNoticeId() { return jobNoticeId; }
    public void setJobNoticeId(int jobNoticeId) { this.jobNoticeId = jobNoticeId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getFilledCount() { return filledCount; }
    public void setFilledCount(int filledCount) { this.filledCount = filledCount; }

    public int getAvailableCount() {
        return Math.max(0, totalCount - filledCount);
    }

    public boolean isAvailable() {
        return getAvailableCount() > 0;
    }
}
