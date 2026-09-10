package com.cvmatcher.model;

/**
 * Represents a certification associated with a Curriculum Vitae (CV).
 * Holds details such as the certification name, issuer, and year of completion.
 */
public class Certification {
    
    private int id;
    private int cvId;
    private String name;
    private String issuer;
    private Integer year;

    /**
     * Default constructor.
     */
    public Certification() {
    }

    /**
     * Constructs a new Certification with the specified details.
     *
     * @param name   The name of the certification.
     * @param issuer The organization that issued the certification.
     * @param year   The year the certification was obtained.
     */
    public Certification(final String name, final String issuer, final Integer year) {
        this.name = name;
        this.issuer = issuer;
        this.year = year;
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

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(final Integer year) {
        this.year = year;
    }
}
