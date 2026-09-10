package com.cvmatcher.model;

/**
 * Represents a professional experience entry associated with a Curriculum Vitae (CV).
 * Holds details such as the company, job title, description of duties, and the duration of employment.
 */
public class Experience {
    
    private int id;
    private int cvId;
    private String company;
    private String title;
    private String description;
    private Integer startYear;
    private Integer endYear;

    /**
     * Default constructor.
     */
    public Experience() {
    }

    /**
     * Constructs a new Experience entry with the specified details.
     *
     * @param company     The name of the company or organization.
     * @param title       The job title or position held.
     * @param description A description of the roles and responsibilities.
     * @param startYear   The year the employment started.
     * @param endYear     The year the employment ended (can be null for current roles).
     */
    public Experience(final String company, final String title, final String description, final Integer startYear, final Integer endYear) {
        this.company = company;
        this.title = title;
        this.description = description;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getCvId() {
        return this.cvId;
    }

    public void setCvId(final int cvId) {
        this.cvId = cvId;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getStartYear() {
        return this.startYear;
    }

    public void setStartYear(final Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getEndYear() {
        return this.endYear;
    }

    public void setEndYear(final Integer endYear) {
        this.endYear = endYear;
    }
}
