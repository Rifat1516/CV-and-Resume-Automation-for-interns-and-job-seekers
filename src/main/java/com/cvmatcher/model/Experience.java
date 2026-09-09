package com.cvmatcher.model;

public class Experience {
    private int id;
    private int cvId;
    private String company;
    private String title;
    private String description;
    private Integer startYear;
    private Integer endYear;

    public Experience() {}

    public Experience(String company, String title, String description, Integer startYear, Integer endYear) {
        this.company = company;
        this.title = title;
        this.description = description;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }
    public Integer getEndYear() { return endYear; }
    public void setEndYear(Integer endYear) { this.endYear = endYear; }
}
