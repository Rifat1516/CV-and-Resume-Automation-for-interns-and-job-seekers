package com.cvmatcher.model;

import com.cvmatcher.model.state.ClosedState;
import com.cvmatcher.model.state.NoticeState;
import com.cvmatcher.model.state.OpenState;
import com.cvmatcher.model.state.PartiallyFilledState;

import java.util.ArrayList;
import java.util.List;

/**
 * Modeled after a real job-vacancy poster: company, title, location,
 * salary, experience required, a free-text description, and separate
 * bullet-point lists for responsibilities vs requirements/qualifications.
 */
public class JobNotice {
    private int id;
    private String company;
    private String title;
    private String location;
    private String salary;
    private String experienceRequired;
    private String description;
    private int totalPositions;
    private int filledPositions;
    private String deadline;
    private String contactEmail;
    private int uploadedByUserId;
    private final List<JobRequirement> requirements = new ArrayList<>();
    private final List<JobPosition> positions = new ArrayList<>();

    public JobNotice() {}

    /** Convenience constructor for the required fields; everything else is optional via setters. */
    public JobNotice(String company, String title, int totalPositions, int uploadedByUserId) {
        this.company = company;
        this.title = title;
        this.totalPositions = totalPositions;
        this.uploadedByUserId = uploadedByUserId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(String experienceRequired) { this.experienceRequired = experienceRequired; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTotalPositions() { return totalPositions; }
    public void setTotalPositions(int totalPositions) { this.totalPositions = totalPositions; }
    public int getFilledPositions() { return filledPositions; }
    public void setFilledPositions(int filledPositions) { this.filledPositions = filledPositions; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public int getUploadedByUserId() { return uploadedByUserId; }
    public void setUploadedByUserId(int uploadedByUserId) { this.uploadedByUserId = uploadedByUserId; }
    public List<JobRequirement> getRequirements() { return requirements; }
    public List<JobPosition> getPositions() { return positions; }

    public List<JobRequirement> getResponsibilities() {
        return requirements.stream()
                .filter(r -> r.getCategory() == RequirementCategory.RESPONSIBILITY)
                .toList();
    }

    public List<JobRequirement> getQualifications() {
        return requirements.stream()
                .filter(r -> r.getCategory() == RequirementCategory.REQUIREMENT)
                .toList();
    }

    public int getAvailablePositions() {
        return Math.max(0, totalPositions - filledPositions);
    }

    /**
     * State pattern: the notice's lifecycle state is derived from its
     * available positions rather than stored as a column, but behavior
     * (is it rankable / visible as available) is delegated to the state
     * object instead of scattered if-statements across the codebase.
     */
    public NoticeState getState() {
        int available = getAvailablePositions();
        if (available <= 0) return new ClosedState();
        if (filledPositions > 0) return new PartiallyFilledState();
        return new OpenState();
    }

    /**
     * Combines every structured field (title, location, experience,
     * free-text description, and every responsibility/requirement bullet)
     * into one blob for the scoring/duplicate-check algorithms -- so those
     * algorithms don't need to know or care about the notice's structure.
     */
    public String toSearchableText() {
        StringBuilder sb = new StringBuilder();
        if (title != null) sb.append(title).append(" ");
        if (location != null) sb.append(location).append(" ");
        if (experienceRequired != null) sb.append(experienceRequired).append(" ");
        if (description != null) sb.append(description).append(" ");
        for (JobRequirement r : requirements) sb.append(r.getDescription()).append(" ");
        return sb.toString();
    }
}
